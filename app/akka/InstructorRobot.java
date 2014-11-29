package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.messages.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by dennyac on 11/8/14.
 */


public class InstructorRobot extends UntypedActor {

    final ActorRef instructorActor;
    final String instructorEmail;
    private static final Logger.ALogger logger = Logger.of(InstructorRobot.class);

    public static Props props(final ActorRef actor, final String instructorEmail) {
        return Props.create(new Creator<InstructorRobot>() {
            private static final long serialVersionUID = 1L;

            @Override
            public InstructorRobot create() throws Exception {
                return new InstructorRobot(actor, instructorEmail);
            }
        });
    }

    public InstructorRobot(final ActorRef actor, final String instructorEmail ) {
        // Join the room
        this.instructorActor = actor;
        this.instructorEmail = instructorEmail;
        logger.info("InstructorRobot(" + instructorEmail + "):Constructor");
//        actor.tell(new Join("Sparky", getSelf(), "Hi! I'm Sparky. I'll be assisting you along the way."), getSelf());
//
//        // Make the robot talk every 30 seconds
//        Akka.system().scheduler().schedule(
//                Duration.create(30, SECONDS),
//                Duration.create(30, SECONDS),
//                actor,
//                new Talk("Sparky", 1l, "I'm still alive"),
//                Akka.system().dispatcher(),
//                /** sender **/getSelf()
//        );

    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String msg = (String) message;
            if(msg.equals("Dummy")){
                instructorActor.tell(new Talk(
                        "Sparky",
                        1l,
                        "Dummy"
                ), getSelf());

            }


        }


    }

}
