package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.messages.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Robot extends UntypedActor {

    public static Props props(final ActorRef actor) {
        return Props.create(new Creator<Robot>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Robot create() throws Exception {
                return new Robot(actor);
            }
        });
    }

    public Robot(ActorRef actor) {

        // Join the room

        actor.tell(new Join("Sparky", getSelf(), "Hi! I'm Sparky. I'll be assisting you along the way."), getSelf());


        // Make the robot talk every 30 seconds
        Akka.system().scheduler().schedule(
                Duration.create(30, SECONDS),
                Duration.create(30, SECONDS),
                actor,
                new Talk("Sparky", 1l, "You haven't posted for a while :( . It would be great if you could contribute to the conversation"),
                Akka.system().dispatcher(),
                /** sender **/getSelf()
        );

    }

    //get events to notify students when they aren't performing
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {

            //do nothing

        }


    }

}
