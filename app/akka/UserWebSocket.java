package akka;

import akka.actor.*;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.RedisPlugin;
import models.Event;
import models.EventActions;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import redis.clients.jedis.Jedis;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.messages.*;

import java.util.Date;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;
import static models.User.findByEmail;

/**
 * Created by dennyac on 11/23/14.
 */


public class UserWebSocket extends UntypedActor {

    public static Props props(final String username, final long eventId, final ActorRef out) {

        return Props.create(new Creator<UserWebSocket>() {
            private static final long serialVersionUID = 1L;

            @Override
            public UserWebSocket create() throws Exception {
                return new UserWebSocket(username, eventId, out);
            }
        });

    }

    private final ActorRef out;
    private final Long eventId;
    private final String username;
    private final String instructorEmail;
    private static final Logger.ALogger logger = Logger.of(UserWebSocket.class);


    public UserWebSocket(final String username, final long eventId, ActorRef out) throws Exception {

        this.eventId = eventId;
        this.username = username;
        this.instructorEmail = Event.findById(eventId).instructor.email;
        final User user = findByEmail(username);
        //chatRooms.get(eventId).tell(new ChatRequest(username,out,in),null);

        String result = (String) Await.result(ask(WebSocketManager.getChatRoom(eventId, instructorEmail), new Join(username, out, user.bio), 3000), Duration.create(3, SECONDS));
        if ("OK".equals(result)) {
            //Things are cool
        } else {
            //Need to throw exception I guess
        }
        this.out = out;
    }

    public void postStop() throws Exception {
        //this.out in constructor comes in handy over here. Is it required?
        WebSocketManager.getChatRoom(eventId, instructorEmail).tell(new Quit(username, out), self());
    }

    public void onReceive(Object message) throws Exception {
        logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:Entry");
        if (message instanceof JsonNode) {

            try
            {
                //JsonNode json = Json.parse((String) message);
                JsonNode json = (JsonNode) message;
                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:JsonNode");
                String uname = json.get("user").asText();
                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:json:username:" + uname);
                Long eId = Long.parseLong(json.get("event").asText());
                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:json:eventId:" + eId.toString());
                String msg = json.get("text").asText();
                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:json:Message:" + msg);

                Talk talk = new Talk(uname, eId, msg);
                EventActions ea = new EventActions();
                ea.ActionType = "Message";
                ea.Attribute1 = msg;
                ea.user = User.findByEmail(uname);
                ea.TimeOfEventAction = DateTime.now();
                ea.Attribute2 = ea.Attribute1.charAt(0) == '#' ? "hashTag" : null;
                ea.event = Event.findById(eId);
                EventActions.asyncSave(ea);

                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:AsyncSave");

                String instructorEmail = ea.event.instructor.email;
                String channel = instructorEmail + ".event." + eventId;
                logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:channel:" + channel);


                Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                try {

                    j.publish(channel, Json.stringify(Json.toJson(talk)));
                    logger.info("UserWebSocket(" + eventId + "." + username + "):onReceive:publish");
                } finally {
                    play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                }
            }
            catch (Exception e) {
                e.printStackTrace();

            }



        }


    }
}