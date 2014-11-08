package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Supriya on 14/10/2014.
 */
public class Event extends Model{

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public Long eventId;

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
    public ArrayList<User> participants;

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
}
