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

    public static class EventAggergator{
        public long eventId;
        public int noOfPraticipants;
        public int totalNoOfMessages;
        public int noOfHashTagMessgaes;
        public int noOfInformalMessages;
        public float percentageCorrectInPhase1;
        public float percentageCorrectInPhase3;
        public float percentageCorrectInPhase4;
        public float positiveCollaborationScore;
        public float negativeCollaborationScore;

    }

    public static class UserAggregator{
        public long userId;
        public long eventId;
        public boolean phase1Answer;
        public boolean phase3Answer;
        public boolean phase4Answer;
        public int noOfIndividualInformalMessages;
        public int getNoOfIndividualHashTagMessages;
        public float scorePhase1;
        public float scorePhase3;
        public float scorePhase4;
    }

    public static EventActions EventAggregator(Event event,User user){
        EventActions e = EventActions.findByEventIDUserIDActionType(event,user,"Stage1");

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
