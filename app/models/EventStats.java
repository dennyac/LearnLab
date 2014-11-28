package models;

import javax.persistence.*;
import javax.validation.Constraint;
import models.Event;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Created by shruthir28 on 11/10/2014.
 */
@Entity
public class EventStats extends Model {

    @Id
    public Long id;

    public Event event;

    /*Work around for oneToOne mapping*/
    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public Long eventId;

    @OneToOne(mappedBy = "eventStats", cascade=CascadeType.ALL)
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int noOfPraticipants;

    public int totalNoOfMessages;

    public int noOfHashTagMessgaes;

    public int noOfInformalMessages;

    public double percentageCorrectInPhase1;

    public double percentageCorrectInPhase3;

    public double percentageCorrectInPhase4;

    public double positiveCollaborationScore;

    public double negativeCollaborationScore;

    public static Model.Finder<Long, EventStats> find = new Model.Finder<Long, EventStats>(Long.class, EventStats.class);

    public static EventStats findByEventName(String EventName)
    {
        Event event=Event.findByName("DBNormalization");
        System.out.println("event id  "+event.eventId);
        EventStats st=find.where().eq("eventId",event.eventId).findUnique();
        System.out.println("event stats id  "+st.id);
        return st;
    }

    public static EventStats findByEvent(Event e){
        System.out.println("ComingHERE");
        EventStats eventStats = find.where().eq("event", e).findUnique();
        System.out.println("This was the event retreieved:" + eventStats.event.eventName);
        return eventStats;
    }
}
