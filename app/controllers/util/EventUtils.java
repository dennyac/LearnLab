package controllers.util;

import models.Event;
import models.EventActions;
import models.User;
import models.UserEventStats;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Supriya on 20/11/2014.
 */
public class EventUtils {
    public static class EventStageform{
        public String eventAction;
        public String answer;
        public String eventId;
//        public String fullName;
        public String justification;
        public List<String> upVotedPeers;
        public Long userId;

    }

    public static EventStatsWrapper EventAggregator(Event event,User user){
        //Mark the active status of the event to 2 (Completed)
        event.updateEventStatus();
        //Update the event
        event.update();
        EventStatsWrapper eventStatsWrapper = new EventStatsWrapper(event);
        System.out.println("In the eventAggregator Function");
        eventStatsWrapper.eventStatsAggregator();
        return eventStatsWrapper;
    }

    public static UserEventStats UserEventAggregator(Event event,User user){
        UserEventStats userEventStats = new UserEventStats();
        return userEventStats;
    }

    public static void initEventStage(EventStageform f){
        EventActions e = new EventActions();
        e.Attribute1 =  f.answer;
        e.ActionType = f.eventAction;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        e.TimeOfEventAction = DateTime.now();
        long eventid = Long.parseLong(f.eventId);
        System.out.println("After parsing, the long event id was:"+ eventid);
        e.event =  Event.findById(eventid);
//        System.out.println("After parsing, the STRING user id was:"+ f.fullName);
//        User u = User.findByFullname(f.fullName);
        User u = User.findById(f.userId);
        System.out.println("The user full name was:"+ u.fullname);
        e.user = u;
        if((f.justification != null)){
            e.Attribute2 = f.justification;
            System.out.println("The Justification was:" + e.Attribute2);
        }
        if((f.upVotedPeers != null) && (f.upVotedPeers.size() > 0)){
           for(int i = 0; i<f.upVotedPeers.size(); i++){
               EventActions e1 = new EventActions();
               e1.event = e.event;
               e1.ActionType = "UpVote";
               e1.user = e.user;
               e1.Attribute1 = f.upVotedPeers.get(i);
               e1.TimeOfEventAction = e.TimeOfEventAction;
               e1.save();
           }
        }
        e.save();
    }
}
