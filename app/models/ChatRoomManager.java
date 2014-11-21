package models;

import akka.actor.*;
import com.avaje.ebean.Ebean;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static models.User.*;

/**
 * Created by dennyac on 11/7/14.
 */
public class ChatRoomManager {



    //public static ActorSystem system = ActorSystem.create("LearnLab");

    private static HashMap<Long, ActorRef> chatRooms = new HashMap<Long,ActorRef>();
    public static void joinChatRoom(final String username, final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) throws Exception {

        final User u = findByEmail(username);
        //final Event currEvent = Event.findById(new Long(1l));
        final Event currEvent = u.EventsParticipated.get(0);
        final User instructor = currEvent.instructor;
        if(!chatRooms.containsKey(currEvent.eventId))
            chatRooms.put(currEvent.eventId, Akka.system().actorOf(ChatRoom.props(currEvent.eventId,instructor.email), currEvent.eventId.toString()));

        //chatRooms.get(eventId).tell(new ChatRequest(username,out,in),null);
        String result = (String) Await.result(ask(chatRooms.get(currEvent.eventId), new ChatRoom.Join(username, out, u.bio), 3000), Duration.create(3, SECONDS));
        if("OK".equals(result)) {

            // For each event received on the socket,
            in.onMessage(new F.Callback<JsonNode>() {
                public void invoke(JsonNode event) {

                    ChatRoom.Talk talk = new ChatRoom.Talk(username, currEvent.eventId, event.get("text").asText());
                    EventActions ea = new EventActions();
                    ea.ActionType = "Message";
                    ea.Attribute1 = event.get("text").asText();
                    ea.user = u;
                    ea.TimeOfEventAction = new Date(System.currentTimeMillis());
                    ea.Attribute2 = ea.Attribute1.charAt(0) == '#'? "hashTag":null;
                    ea.event = currEvent;
                    EventActions.asyncSave(ea);

                    Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                    try {
                        //All messages are pushed through the pub/sub channel
                        System.out.println(instructor.email + ".event." + currEvent.eventId);
                        j.publish(instructor.email + ".event." + currEvent.eventId, Json.stringify(Json.toJson(talk)));
                    } finally {
                        play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                    }

                }
            });

            // When the socket is closed.
            in.onClose(new F.Callback0() {
                public void invoke() {

                    // Send a Quit message to the room.
                    chatRooms.get(currEvent.eventId).tell(new ChatRoom.Quit(username, out), null);

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
