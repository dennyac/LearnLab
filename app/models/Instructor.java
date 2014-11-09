package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A chat room is an Actor.
 */
public class Instructor extends UntypedActor {

    // Default room.
    private final String CHANNEL;
    private final WebSocket.Out<JsonNode> out;

    public static Props props(final String instructor, final WebSocket.Out<JsonNode> out) {
        return Props.create(new Creator<Instructor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Instructor create() throws Exception {
                return new Instructor(instructor, out);
            }
        });
    }

    public Instructor(final String instructor, final WebSocket.Out<JsonNode> out){
        CHANNEL = instructor + ".event.*";
        this.out = out;
        //add the robot


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

        Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.SECONDS),
                new Runnable() {
                    public void run() {
                        new InstructorRobot(getSelf());
                    }
                },
                Akka.system().dispatcher()
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
        if(message instanceof ChatRoom.Talk)  {
                // Received a ChatRoom.Talk message
                ChatRoom.Talk talk = (ChatRoom.Talk)message;
                out.write(Json.toJson(talk));

            } else {
                unhandled(message);
            }
        } 


    public class MyListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String messageBody) {
            //Process messages from the pub/sub channel
            JsonNode parsedMessage = Json.parse(messageBody);
            Object message = null;
            String messageType = parsedMessage.get("type").asText();
            if("talk".equals(messageType)) {
                message = new ChatRoom.Talk(
                        parsedMessage.get("username").asText(),
                        parsedMessage.get("eventId").asText(),
                        parsedMessage.get("text").asText()
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
