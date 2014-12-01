package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
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

/**
 * Created by dennyac on 11/23/14.
 */


public class InstructorWebSocket extends UntypedActor {

    public static Props props(final String username, final ActorRef socketOut) {

        return Props.create(new Creator<InstructorWebSocket>() {
            private static final long serialVersionUID = 1L;

            @Override
            public InstructorWebSocket create() throws Exception {
                return new InstructorWebSocket(username, socketOut);
            }
        });

    }

    private final ActorRef socketOut;
    private final String instructorEmail;
    private static final Logger.ALogger logger = Logger.of(InstructorWebSocket.class);


    public InstructorWebSocket(final String instructorEmail, final ActorRef socketOut) throws Exception {
        this.instructorEmail = instructorEmail;
        //chatRooms.get(eventId).tell(new ChatRequest(username,out,in),null);

        String result = (String) Await.result(ask(WebSocketManager.getInstructor(instructorEmail), new Join(instructorEmail, socketOut, null), 3000), Duration.create(3, SECONDS));
//        if ("OK".equals(result)) {
//            //Things are cool
//
//        }
//        else{
//            //Need to throw exception I guess
//        }
        this.socketOut = socketOut;
    }

    public void postStop() throws Exception {
        //this.out in constructor comes in handy over here. Is it required?
        WebSocketManager.getInstructor(instructorEmail).tell(new Quit(instructorEmail, socketOut), self());
    }

    public void onReceive(Object message) throws Exception {
        logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:Entry");
        if (message instanceof JsonNode) {

            try{
                logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:JsonNode");
                JsonNode json = (JsonNode) message;
                Long eId = Long.parseLong(json.get("eventid").asText());
                logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:json:eventId:" + eId.toString());
                String msg = json.get("text").asText();
                logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:json:Message:" + msg);

                Talk talk = new Talk(instructorEmail, 1L, msg);

                String channel = instructorEmail + ".event." + eId.toString();

                EventActions ea = new EventActions();
                ea.ActionType = "InstructorMessage";
                ea.Attribute1 = msg;
                ea.user = User.findByEmail(instructorEmail);
                ea.TimeOfEventAction = DateTime.now();
                ea.event = Event.findById(eId);
                EventActions.asyncSave(ea);

                logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:AsyncSave");

                Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                try {
                    //All messages are pushed through the pub/sub channel

                    j.publish(channel, Json.stringify(Json.toJson(talk)));
                    logger.info("InstructorWebSocket(" + instructorEmail + "):onReceive:channel:" + channel + ":publish");

                } finally {
                    play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                }
            }catch(Exception e){
                e.printStackTrace();
            }




        }


    }
}