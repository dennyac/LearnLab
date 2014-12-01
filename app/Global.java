import akka.JobScheduler;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.*;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by dennyac on 11/30/14.
 */


public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger.info("Application has started");
        scheduleJobs();
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

    private void scheduleJobs(){
        ActorRef jobScheduler = Akka.system().actorOf(Props.create(JobScheduler.class),"jobScheduler");
        Akka.system().scheduler().schedule(
                Duration.create(5, SECONDS),
                Duration.create(15, SECONDS),
                jobScheduler,
                "ComputeStats",
                Akka.system().dispatcher(),
                null
        );
    }

}