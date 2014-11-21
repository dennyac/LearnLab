package controllers.util;

import models.Event;
import models.EventActions;
import models.User;
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
        public String fullName;
        public String Justification;
        public List<String> peers;
    }

    public static EventActions EventAggregator(Event event,User user){
        EventActions e = EventActions.findByEventIDUserIDActionType(event,user,"Stage1");
        System.out.println("Got Event Actions" + e.Attribute1);
        return e;
    }

    public static void initEventStage(EventStageform f){
        EventActions e = new EventActions();
        e.Attribute1 =  f.answer;
        e.ActionType = f.eventAction;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        e.TimeOfEventAction = date;
        long eventid = Long.parseLong(f.eventId);
        System.out.println("After parsing, the long event id was:"+ eventid);
        e.event =  Event.findById(eventid);
        System.out.println("After parsing, the STRING user id was:"+ f.fullName);
        User u = User.findByFullname(f.fullName);
        System.out.println("The user full name was:"+ u.fullname);
        e.user = u;
        e.save();
    }
}
