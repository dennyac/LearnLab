package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.typesafe.plugin.RedisPlugin;
import models.Event;
import models.EventActions;
import models.User;
import play.Logger;
import play.libs.Akka;
import redis.clients.jedis.Jedis;
import scala.concurrent.duration.Duration;
import akka.messages.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Robot extends UntypedActor {

    //final ArrayList<IndividualMessage> messages;
    final ActorRef chatRoomActor;
    final Event event;
    private static final Logger.ALogger logger = Logger.of(Robot.class);

    public static Props props(final ActorRef actor, final Long eventId) {
        return Props.create(new Creator<Robot>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Robot create() throws Exception {
                return new Robot(actor,eventId);
            }
        });
    }

    public Robot(final ActorRef chatRoomActor, final Long eventId) {

        // Join the room
        //messages = new ArrayList<>();
        this.chatRoomActor = chatRoomActor;
        this.event = Event.findById(eventId);

        //actor.tell(new Join("Sparky", getSelf(), ), getSelf());

        logger.info("Robot(" + event.eventId + "):Constructor");

        // Make the robot talk every 30 seconds


//        Akka.system().scheduler().scheduleOnce(
//                Duration.create(10, TimeUnit.MILLISECONDS),
//                new Runnable() {
//                    public void run() {
//                        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
//                        j.subscribe(new MyListener(), CHANNEL);
//                    }
//                },
//                Akka.system().dispatcher()
//        );

    }

    //get events to notify students when they aren't performing
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String msg = (String) message;
            if(msg.equals("BatchMessages")) {
                //logger.info("Robot(" + event.eventId + "):onReceive:BatchMessages");
                HashSet<User> inactiveUsers = new HashSet<User>(event.participants);
                HashSet<User> activeUsers = new HashSet<User>();

                for(EventActions ea:EventActions.getEventMessages(event,2)){
                    logger.info("Robot(" + event.eventId + "):onReceive:BatchMessages:ActiveUser:" + ea.user.id);
                    activeUsers.add(ea.user);
                }
                inactiveUsers.removeAll(activeUsers);
                for(User inactiveUser: inactiveUsers){
                    logger.info("Robot(" + event.eventId + "):onReceive:BatchMessages:InactiveUser:" + inactiveUser.id);
                    chatRoomActor.tell(new IndividualMessage(inactiveUser.email,"You haven't posted for a while :( . It would be great if you could contribute to the conversation"),getSelf());
                }
                logger.info("Robot(" + event.eventId + "):onReceive:BatchMessages:SendMessages");
                //messages.clear();
            }
            else if(msg.equals("Dummy")){
                chatRoomActor.tell(new RosterNotification("Sparky", "Dummy", "Dummy Message"), getSelf());

            }

            //do nothing

        }
        else if (message instanceof Join){
            Join join = (Join) message;
            logger.info("Robot(" + event.eventId + "):onReceive:Join:" + join.getUsername());
            chatRoomActor.tell(new IndividualMessage(join.getUsername(), "Hi! I'm Sparky. I'll be assisting you along the way."),getSelf());

        }


    }

}
