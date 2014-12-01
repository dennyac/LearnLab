package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.messages.Talk;
import controllers.util.EventStatsWrapper;
import controllers.util.EventUtils;
import models.Event;
import play.Logger;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.messages.*;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by dennyac on 11/30/14.
 */


public class JobScheduler extends UntypedActor {


    private static final Logger.ALogger logger = Logger.of(JobScheduler.class);


    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            String msg = (String) message;
            if(msg.equals("ComputeStats")){
                logger.info("JobScheduler: Received ComputeStats message");
                List<Event> events = Event.findAllEventsToBeAggregated();

                for(Event e: events) {
                    if (Event.isEventReadyForAggregation(e)) {
                        System.out.println("Aggregator called for the event"+e.eventName);
                        EventUtils.EventAggregator(e, null);
                    }
                }

                logger.info("JobScheduler: Completed computing stats");
            }
        }
    }

}
