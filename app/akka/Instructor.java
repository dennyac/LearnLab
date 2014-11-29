package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.RedisPlugin;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import akka.messages.*;


import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A chat room is an Actor.
 */

public class Instructor extends UntypedActor {

    // Default room.
    private final String CHANNEL;
    private final ActorRef InstructorSparky;
    private static final Logger.ALogger logger = Logger.of(Instructor.class);
    private final ArrayList<ActorRef> outSockets = new ArrayList<ActorRef>();

    public static Props props(final String instructor) {
        return Props.create(new Creator<Instructor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Instructor create() throws Exception {
                return new Instructor(instructor);
            }
        });
    }

    public Instructor(final String instructor) {
        CHANNEL = instructor + ".event.*";
        logger.info("Instructor(" + CHANNEL + "):Instructor");

        InstructorSparky = Akka.system().actorOf(InstructorRobot.props(getSelf(),instructor), CHANNEL + ".InstructorSparky");
        //add the robot


        //subscribe to the message channel
        Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.MILLISECONDS),
                new Runnable() {
                    public void run() {
                        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                        j.psubscribe(new MyListener(), CHANNEL);
                    }
                },
                Akka.system().dispatcher()
        );

        Akka.system().scheduler().schedule(
                Duration.create(20, SECONDS),
                Duration.create(20, SECONDS),
                InstructorSparky,
                "Dummy",
                Akka.system().dispatcher(),
                /** sender **/getSelf()
        );
    }

    /**
     * Join the default room.
     */

    private void remoteMessage(Object message) {
        getSelf().tell(message, null);
    }

    // Users connected to this node

    public void onReceive(Object message) throws Exception {
        if (message instanceof Join) {
            // Received a Join message
            Join join = (Join) message;
            logger.info("Instructor(" + CHANNEL + "):onReceive:Join:" + join.getUsername());

            outSockets.add(join.getOutSocket());
            logger.info("Instructor(" + CHANNEL + "):onReceive:Join:" + join.getUsername() + ": Websocket added to list");

            getSender().tell("OK", getSelf());

        } else if (message instanceof Talk) {
            // Received a ChatRoom.Talk message
            Talk talk = (Talk) message;
            logger.info("Instructor(" + CHANNEL + "):onReceive:Talk:" + talk.getUsername() + ":eventId:" + talk.getEventId() + ":Message:" + talk.getText());

            for (ActorRef outSocket : outSockets)
                outSocket.tell(Json.toJson(talk), getSelf());
            logger.info("Instructor(" + CHANNEL + "):onReceive:Talk:" + talk.getUsername() + ":eventId:" + talk.getEventId() + ":Message:" + talk.getText() + ":Sent message to all websockets");


        } else if (message instanceof Quit) {
            // Received a Quit message
            Quit quit = (Quit) message;
            logger.info("Instructor(" + CHANNEL + "):onReceive:Quit:" + quit.getUsername());
            //Remove the member from this node and the global roster

            outSockets.remove(quit.getOutSocket());

        } else {
            logger.info("Instructor(" + CHANNEL + "):onReceive:Unhandled");
            unhandled(message);
        }
    }


    public class MyListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String messageBody) {
        }

        @Override
        public void onPMessage(String arg0, String arg1, String arg2) {

            //Process messages from the pub/sub channel
            logger.info("Instructor(" + CHANNEL + "):MyListener:onPMessage:" + arg0 + ":" + arg1);
            JsonNode parsedMessage = Json.parse(arg2);
            Object message = null;
            String messageType = parsedMessage.get("type").asText();
            if ("talk".equals(messageType)) {
                logger.info("Instructor(" + CHANNEL + "):MyListener:onPMessage:" + arg0 + ":" + arg1 + ":" + messageType + ":" + parsedMessage.get("username").asText() + ":" + parsedMessage.get("eventId").asText() + ":" + parsedMessage.get("text").asText());
                message = new Talk(
                        parsedMessage.get("username").asText(),
                        Long.parseLong(parsedMessage.get("eventId").asText()),
                        parsedMessage.get("text").asText()
                );
                remoteMessage(message);
            }

        }

        @Override
        public void onPSubscribe(String arg0, int arg1) {
        }

        @Override
        public void onPUnsubscribe(String arg0, int arg1) {
        }

        @Override
        public void onSubscribe(String arg0, int arg1) {
        }

        @Override
        public void onUnsubscribe(String arg0, int arg1) {
        }
    }

}
