package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;
import play.db.ebean.Model;

/**
 * Created by Supriya on 14/10/2014.
 */
@Entity
public class Question extends Model {
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public Long questionNumber;

    @ManyToMany(mappedBy = "Questions")
    public List<Event> EventsUsingQuestion;

    @Constraints.Required
    @Formats.NonEmpty
    public String Question;

    @Constraints.Required
    @Formats.NonEmpty
    public String Option1;

    @Constraints.Required
    @Formats.NonEmpty
    public String Option2;

    @Constraints.Required
    @Formats.NonEmpty
    public String Option3;

    @Constraints.Required
    @Formats.NonEmpty
    public String Option4;

    @Constraints.Required
    @Formats.NonEmpty
    public String Answer;

    public static models.Question getQuestionByEventId(){
        models.Question q = new models.Question();
        return q;
    }
}
