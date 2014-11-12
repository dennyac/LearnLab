package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public static Model.Finder<Long, User> ufind = new Model.Finder<Long, User>(Long.class, User.class);
    public static Model.Finder<Long, Event> efind = new Model.Finder<Long, Event>(Long.class, Event.class);
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String eventId;

    @Constraints.Required
    @Formats.NonEmpty
    public String eventName = "DB Normalization";

    @Constraints.Required
    @Formats.NonEmpty
    public String script = "The event contains 3 stages. Stage 1: Answer the question that is being displayed. Stage 2: Collaborate with you event-mates and brainstorm about the answer options.Use Hashtags to anything that might be relevant. Stage 3: Having discussed, conclude the right answer and submit it. Give a short justification as to why you pick the answer. Also let us know who helped you better to arrive at the answer";

    @Constraints.Required
    @Formats.NonEmpty
    public HashSet<String> hashTags;

    @Constraints.Required
    @Formats.NonEmpty
    public ArrayList<String> participants;

    @Constraints.Required
    @Formats.NonEmpty
    public List Question;

    public static Event findEvent(){
        Event e = new Event();
        HashSet<String> tags = new HashSet<String>();
        tags.add("#Concept");
        tags.add("#Justification");
        tags.add("#Examples");
        return e;
    }

    public static void populateEvents(){
        HashSet<String> tags = new HashSet<String>();
        ArrayList<String> particpants = new ArrayList<String>();
//        List<User> userlist = ufind.findList();
//        for(int i=0;<userlist.size();i++){
//
//        }
        particpants.add("ANil");
        particpants.add("denny");
        particpants.add("supriya");
        particpants.add("dhanyatha");
        particpants.add("shruthi");
        ArrayList<String> questions = new ArrayList<String>();
        questions.add("What is the diff b/w database and schema");
        questions.add("can a database multiple schemas");
        tags.add("#Concept");
        tags.add("#Justification");
        tags.add("#Examples");
        Event e = new Event();
        e.eventId = "EventGroupId4";
        e.eventName = "EventGroup5";
        e.hashTags = tags;
        e.participants = particpants;
        e.Question = questions;
        e.script = "New script";
        e.save();
    }

    public static List<Event> getEventList() {
        List<Event> list = efind.findList();
        return list;
    }


}
