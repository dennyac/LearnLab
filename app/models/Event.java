package models;

import com.avaje.ebean.Ebean;
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

import models.utils.AppException;
import models.utils.Hash;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

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
    @ManyToOne
    public User instructor;

    @Constraints.Required
    @Formats.NonEmpty
    //@Column(unique = true)
    public String eventName;

    @Formats.DateTime(pattern = "MM/dd/yyyy")
    public Date EventDate;

    @Constraints.Required
    @Formats.NonEmpty
    public String startTime;

    @Constraints.Required
    @Formats.NonEmpty
    public String endTime;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(columnDefinition = "TEXT")
    public String description;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(columnDefinition = "TEXT")
    public String scriptPhase1;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(columnDefinition = "TEXT")
    public String scriptPhase2;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(columnDefinition = "TEXT")
    public String scriptPhase3;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(columnDefinition = "TEXT")
    public String scriptPhase4;

    @Constraints.Required
    @Formats.NonEmpty
    public String hashes;

    @Constraints.Required
    @Formats.NonEmpty
    @ManyToMany
    public List<User> participants;

    @Constraints.Required
    @Formats.NonEmpty
    @ManyToMany
    public List<Question> Questions;

    @Constraints.Required
    @Formats.NonEmpty
    public long phase1Duration; // needs to be minutes

    @Constraints.Required
    @Formats.NonEmpty
    public long phase2Duration;

    @Constraints.Required
    @Formats.NonEmpty
    public long phase3Duration;



    public boolean active;  //flag to check if the event is an ongoing event.

    @OneToOne
    public EventStats eventStats;

    @OneToMany(mappedBy = "event")
    public List<EventActions> eventActions;

    @OneToMany(mappedBy = "event")
    public List<UserEventStats> listOfUserEventStats;

    public static Model.Finder<Long, Event> find = new Model.Finder<Long, Event>(Long.class, Event.class);

    public static Event findById(Long eId ) {

        //return find.where().eq("eventId", eId ).findUnique();
        return Event.find.byId(eId);
    }

    public static List<Event> findAllEvents()
    {
        return find.findList();
    }

        public static void testList()
    {
        List<Event> u = findAllEvents();
        for( Event ur : u)
        {
            System.out.println(" the name is " + ur.eventName);
        }
    }

    public static Event findByName(String EventName)
    {
        return find.where().eq("eventName",EventName).findUnique();
    }


   /* public static List<Event> getActiveEventNameList()
    {

        long minTotal =  (phase3Duration +phase3Duration +phase2Duration);

        Date d = new Date(); //current date
        Date d1 = new Date(d.getTime()+ TimeUnit.MINUTES.toMillis(minTotal));  //end time

        return find.where().ge("EventStartTime" , d).lt("EventStartTime" , d1).findList();

    }*/

    public static List<User> findEventParticipants(Event event) {
        List<User> participants = find.where().eq("event",event).findUnique().participants;
        return participants;
    }

    public static String getNumberofCorrectAnswersPhase1(String EventName) {

        List<Question> list = findByName(EventName).Questions;
        return list.get(0).Answer;
    }

    public static String getNumberofCorrectAnswersPhase2(String EventName) {

        List<Question> list = findByName(EventName).Questions;
        return list.get(1).Answer;
    }

    public String[] getHashTags()
    {
        String[] hashtags=this.hashes.split("#");
        for(String s: hashtags)
        {
            System.out.println("hashtag :"+ s);
        }
        return hashtags;
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
