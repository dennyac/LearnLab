package models;

import akka.actor.*;
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
import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;

import static models.User.*;

/**
 * Created by dennyac on 11/7/14.
 */
public class ChatRoomManager {



    //public static ActorSystem system = ActorSystem.create("LearnLab");

    private static HashMap<String, ActorRef> chatRooms = new HashMap<String,ActorRef>();
    public static void joinChatRoom(final String username, final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) throws Exception {
        User u = findByEmail(username);
        final String eventId = username.substring(0,2);
        final String instructor = "ins@gmail.com";
        if(!chatRooms.containsKey(eventId))
            chatRooms.put(eventId, Akka.system().actorOf(ChatRoom.props(eventId,instructor), eventId));

        //chatRooms.get(eventId).tell(new ChatRequest(username,out,in),null);
        String result = (String) Await.result(ask(chatRooms.get(eventId), new ChatRoom.Join(username, out), 3000), Duration.create(3, SECONDS));
        if("OK".equals(result)) {

            // For each event received on the socket,
            in.onMessage(new F.Callback<JsonNode>() {
                public void invoke(JsonNode event) {

                    ChatRoom.Talk talk = new ChatRoom.Talk(username, eventId, event.get("text").asText());
                    Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                    try {
                        //All messages are pushed through the pub/sub channel
                        j.publish(instructor + ".event." + eventId, Json.stringify(Json.toJson(talk)));
                    } finally {
                        play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                    }

                }
            });

            // When the socket is closed.
            in.onClose(new F.Callback0() {
                public void invoke() {

                    // Send a Quit message to the room.
                    chatRooms.get(eventId).tell(new ChatRoom.Quit(username, out), null);

                }
            });

        } else {

            // Cannot connect, create a Json error.
            ObjectNode error = Json.newObject();
            error.put("error", result);

            // Send the error to the socket.
            out.write(error);

        }
    }
}
