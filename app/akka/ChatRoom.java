package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.duration.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.messages.*;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * A chat room is an Actor.
 */
public class ChatRoom extends UntypedActor {

    // Default room.
    private final String CHANNEL;
    private final String MEMBERS;
    private final ActorRef sparky;
    private static final ALogger logger = Logger.of(ChatRoom.class);

    public static Props props(final Long eventId, final String instructor) {
        return Props.create(new Creator<ChatRoom>() {
            private static final long serialVersionUID = 1L;

            @Override
            public ChatRoom create() throws Exception {
                return new ChatRoom(eventId, instructor);
            }
        });
    }

    public ChatRoom(final Long eventId, final String instructor) {
        CHANNEL = instructor + ".event." + eventId;
        logger.info("ChatRoom(" + CHANNEL + "):ChatRoom");


        sparky = Akka.system().actorOf(Robot.props(getSelf(),eventId), CHANNEL + ".Sparky");

        logger.info("ChatRoom(" + CHANNEL + "):ChatRoom:Created Sparky");


        MEMBERS = "members." + eventId;

        //subscribe to the message channel
        Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.MILLISECONDS),
                new Runnable() {
                    public void run() {
                        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                        j.subscribe(new MyListener(), CHANNEL);
                    }
                },
                Akka.system().dispatcher()
        );

        Akka.system().scheduler().schedule(
                Duration.create(5, SECONDS),
                Duration.create(60, SECONDS),
                sparky,
                "BatchMessages",
                Akka.system().dispatcher(),
                /** sender **/getSelf()
        );

        //dummy message to keep websockets alive
        Akka.system().scheduler().schedule(
                Duration.create(20, SECONDS),
                Duration.create(20, SECONDS),
                sparky,
                "Dummy",
                Akka.system().dispatcher(),
                /** sender **/getSelf()
        );

        logger.info("ChatRoom(" + CHANNEL + "):ChatRoom:Created Sparky scheduler");

//        Akka.system().scheduler().scheduleOnce(
//                Duration.create(10, TimeUnit.SECONDS),
//                new Runnable() {
//                    public void run() {
//                        Akka.system().actorOf(Robot.props(getSelf()), CHANNEL + ".Sparky");
//                    }
//                },
//                Akka.system().dispatcher()
//        );
    }

    /**
     * Join the default room.
     */

    private void remoteMessage(Object message) {
        getSelf().tell(message, null);
    }

    // Users connected to this node
    Map<String, ArrayList<ActorRef>> members = new HashMap<String, ArrayList<ActorRef>>();

    public void onReceive(Object message) throws Exception {
        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
        try {
            if (message instanceof Join) {
                // Received a Join message
                Join join = (Join) message;
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + join.getUsername());

                if (members.containsKey(join.getUsername())) {
                    members.get(join.getUsername()).add(join.getOutSocket());
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + join.getUsername() + ": Joined from different source");
                } else {
                    ArrayList<ActorRef> sockets = new ArrayList<ActorRef>();
                    sockets.add(join.getOutSocket());
                    members.put(join.getUsername(), sockets);
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + join.getUsername() + ": Joined for the first time");
                    sparky.tell(join,getSelf());

                    j.sadd(MEMBERS, join.getUsername());

                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + join.getUsername() + ":Sent join message to Sparky");

                    //Publish the join notification to all nodes
                    RosterNotification rosterNotify = new RosterNotification(join.getUsername(), "join", join.getMessage());
                    j.publish(CHANNEL, Json.stringify(Json.toJson(rosterNotify)));
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + join.getUsername() + ":RosterMessage:" + join.getMessage());
                }


                getSender().tell("OK", getSelf());

            } else if(message instanceof  IndividualMessage){
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:IndividualMessage");
                IndividualMessage msg = (IndividualMessage) message;
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:IndividualMessage1:" + msg.getUsername() + ":" + msg.getText()+":"+getSender());
                if(members.containsKey(msg.getUsername())){
                    for(ActorRef socketOut: members.get(msg.getUsername())){
                        logger.info("ChatRoom(" + CHANNEL + "):onReceive:IndividualMessage:"+msg.getUsername()+":"+msg.getText());
                        ObjectNode event = Json.newObject();
                        event.put("kind", "talk");
                        event.put("user", "Sparky");
                        event.put("message", msg.getText());
                        socketOut.tell(event, self());
                    }
                }
            } else if (message instanceof Quit) {
                // Received a Quit message
                Quit quit = (Quit) message;
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:Quit:" + quit.getUsername());
                //Remove the member from this node and the global roster
                members.get(quit.getUsername()).remove(quit.getOutSocket());
                j.srem(MEMBERS, quit.getUsername());
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:Quit:" + quit.getUsername() + ": Removed user from REDIS set and associated websocket(s)");

                //Publish the quit notification to all nodes
                RosterNotification rosterNotify = new RosterNotification(quit.getUsername(), "quit", null);
                j.publish(CHANNEL, Json.stringify(Json.toJson(rosterNotify)));
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:Join:" + quit.getUsername() + ": Send quit roster notification on REDIS channel");
            } else if (message instanceof RosterNotification) {

                //Received a roster notification
                RosterNotification rosterNotify = (RosterNotification) message;
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:RosterNotification:" + rosterNotify.getUsername() + ":" + rosterNotify.getDirection());


                if ("join".equals(rosterNotify.getDirection())) {
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:RosterNotification:" + rosterNotify.getUsername() + ":Join");
                    notifyAll("join", rosterNotify.getUsername(), rosterNotify.getMessage());
                } else if ("quit".equals(rosterNotify.getDirection())) {
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:RosterNotification:" + rosterNotify.getUsername() + ":Quit");
                    notifyAll("quit", rosterNotify.getUsername(), "has left the room");
                } else if("Dummy".equals(rosterNotify.getDirection())) {
                    logger.info("ChatRoom(" + CHANNEL + "):onReceive:RosterNotification:" + rosterNotify.getUsername() + ":Dummy");
                    notifyAll("Dummy", rosterNotify.getUsername(), "Dummy Message");
                }
            } else if (message instanceof Talk) {
                // Received a Talk message
                Talk talk = (Talk) message;
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:Talk:" + talk.getUsername() + ":" + talk.getText());
                notifyAll("talk", talk.getUsername(), talk.getText());

            } else {
                logger.info("ChatRoom(" + CHANNEL + "):onReceive:UnhandledMessage");
                unhandled(message);
            }
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }

    }

    // Send a Json event to all members connected to this node
    public void notifyAll(String kind, String user, String text) {
        logger.info("ChatRoom(" + CHANNEL + "):notifyAll:" + kind + ":" + user + ":" + text);
        for (ArrayList<ActorRef> outSockets : members.values()) {
            for (ActorRef socketOut : outSockets) {
                ObjectNode event = Json.newObject();
                event.put("kind", kind);
                event.put("user", user);
                event.put("message", text);

//                ArrayNode m = event.putArray("members");
//
//                //Go to Redis to read the full roster of members. Push it down with the message.
//                Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
//                try {
//                    for(String u: j.smembers(MEMBERS)) {
//                        m.add(u);
//                    }
//                } finally {
//                    play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
//                }
                socketOut.tell(event, self());

            }
        }
        logger.info("ChatRoom(" + CHANNEL + "):notifyAll:" + kind + ":" + user + ":" + text + ":" + "Sent to all websockets belonging to dyno");
    }


    public class MyListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String messageBody) {
            logger.info("ChatRoom(" + CHANNEL + "):MyListener:onMessage:" + channel);
            //Process messages from the pub/sub channel
            JsonNode parsedMessage = Json.parse(messageBody);
            Object message = null;
            String messageType = parsedMessage.get("type").asText();
            if ("talk".equals(messageType)) {
                logger.info("ChatRoom(" + CHANNEL + "):MyListener:onMessage:" + messageType + ":" + parsedMessage.get("username").asText() + ":" + parsedMessage.get("eventId").asText() + ":" + parsedMessage.get("text").asText());
                message = new Talk(
                        parsedMessage.get("username").asText(),
                        Long.parseLong(parsedMessage.get("eventId").asText()),
                        parsedMessage.get("text").asText()
                );
            } else if ("rosterNotify".equals(messageType)) {
//
                logger.info("ChatRoom(" + CHANNEL + "):MyListener:onMessage:" + messageType + ":" + parsedMessage.get("username").asText() + ":" + parsedMessage.get("direction").asText() + ":" + parsedMessage.get("message").asText());
                message = new RosterNotification(
                        parsedMessage.get("username").asText(),
                        parsedMessage.get("direction").asText(),
                        parsedMessage.get("message").asText()
                );
            }
            remoteMessage(message);
        }

        @Override
        public void onPMessage(String arg0, String arg1, String arg2) {
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
