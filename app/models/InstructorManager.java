package models;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.HashMap;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;
import static models.User.findByEmail;


/**
 * Created by dennyac on 11/8/14.
 */
public class InstructorManager {



    //public static ActorSystem system = ActorSystem.create("LearnLab");

    private static HashMap<String, ActorRef> instructors = new HashMap<String,ActorRef>();
    public static void createInstructor(final String username, final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) throws Exception {
        User u = findByEmail(username);

        if(!instructors.containsKey(username))
            instructors.put(username, Akka.system().actorOf(Instructor.props(username, out), username));

            // For each event received on the socket,
            in.onMessage(new F.Callback<JsonNode>() {
                //todo need to send event details in json message
                public void invoke(JsonNode event) {

                   // ChatRoom.Talk talk = new ChatRoom.Talk(username, event.get("eventId").asText(), event.get("text").asText());
                    //todo need to send event details in json message
                    ChatRoom.Talk talk = new ChatRoom.Talk(username, 1L, event.get("text").asText());

                    Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                    try {
                        //All messages are pushed through the pub/sub channel
                        //todo need to send event details in json message
                        j.publish(username + ".event." + "te", Json.stringify(Json.toJson(talk)));
                        System.out.println(username + ".event." + "te");
                    } finally {
                        play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                    }

                }
            });

//            // When the socket is closed.
            in.onClose(new F.Callback0() {
                public void invoke() {

                    System.out.println("Websocket closed");
                    instructors.get(username).tell(new ChatRoom.Quit(username, out), null);

                }
            });

    }
}
