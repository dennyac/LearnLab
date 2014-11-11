package models;

import org.joda.time.Minutes;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Supriya on 14/10/2014.
 */
@Entity
public class Event extends Model{

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public Long eventId;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String eventName = "DB Normalization";

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date EventStartTime;

    @Constraints.Required
    @Formats.NonEmpty
<<<<<<< HEAD
    public String script = "The event contains 3 stages Stage 1: Answer the question that is being displayed. Stage 2: Collaborate with you event-mates and brainstorm about the answer options.Use Hashtags to anything that might be relevant. Stage 3: Having discussed, conclude the right answer and submit it. Give a short justification as to why you pick the answer. Also let us know who helped you better to arrive at the answer";
=======
    public String script = "The event contains 3 stages. Stage 1: Answer the question that is being displayed. Stage 2: Collaborate with you event-mates and brainstorm about the answer options.Use Hashtags to anything that might be relevant. Stage 3: Having discussed, conclude the right answer and submit it. Give a short justification as to why you pick the answer. Also let us know who helped you better to arrive at the answer";
    public String scriptPhase1;
    public String scriptPhase2;
    public String scriptPhase3;
>>>>>>> origin/master

    @Constraints.Required
    @Formats.NonEmpty
    public HashSet<String> hashTags;

    @Constraints.Required
    @Formats.NonEmpty
    @ManyToMany
    public ArrayList<User> participants;

    @Constraints.Required
    @Formats.NonEmpty
    @ManyToMany
    public List<Question> Questions;

    @Constraints.Required
    @Formats.NonEmpty
    public static long phase1Duration; // needs to be minutes

    @Constraints.Required
    @Formats.NonEmpty
    public static long phase2Duration;

    @Constraints.Required
    @Formats.NonEmpty
    public static long phase3Duration;

    public boolean active;  //flag to check if the event is an ongoing event.

    @OneToOne
    public EventStats eventStats;

    @OneToMany(mappedBy = "event")
    public List<EventActions> eventActions;



    public static Model.Finder<Long, Event> find = new Model.Finder<Long, Event>(Long.class, Event.class);

    public static Event findById(String eventId ) {
        return find.where().eq("eventId",eventId ).findUnique();
    }

    public static Event findByName(String EventName)
    {
        return find.where().eq("eventName",EventName).findUnique();
    }


    public static List<Event> getActiveEventNameList()
    {

        long minTotal =  (phase3Duration +phase3Duration +phase2Duration);

        Date d = new Date(); //current date
        Date d1 = new Date(d.getTime()+ TimeUnit.MINUTES.toMillis(minTotal));  //end time

        return find.where().ge("EventStartTime" , d).lt("EventStartTime" , d1).findList();

    }

    public static String getNumberofCorrectAnswersPhase1(String EventName) {

        List<Question> list = findByName(EventName).Questions;
        return list.get(0).Answer;
    }

    public static String getNumberofCorrectAnswersPhase2(String EventName) {

        List<Question> list = findByName(EventName).Questions;
        return list.get(1).Answer;
    }
    public static Event findEvent(){
        Event e = new Event();
        HashSet<String> tags = new HashSet<String>();
        tags.add("#Concept");
        tags.add("#Justification");
        tags.add("#Examples");
        return e;
    }
}
