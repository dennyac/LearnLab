package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;
import play.db.ebean.Model;
/**
 * Created by Supriya on 22/11/2014.
 */

@Entity
public class UserEventStats extends Model{
    @Id
    public Long userEventId;

    @ManyToOne
    public User user;

    @ManyToOne
    public Event event;

    @ManyToOne
    public UserStats userStats;

    public boolean phase1AnswerInEvent;

    public boolean phase3AnswerInEvent;

    public boolean phase4AnswerInEvent;

    public int noOfIndividualInformalMessagesInEvent;

    public int noOfIndividualHashTagMessagesInEvent;

    public double percentageContributionForDiscussionInEvent;

    public double scorePhase1InEvent;

    public double scorePhase3InEvent;

    public double scorePhase4InEvent;

    public double aggregatedScoreForEvent;

    public int cognitiveAbilityScore;

    public double percentageOfCognitiveAbility;

    public double collaborativeIndexForEvent;

    public int noOfUpVotesReceivedForEvent;

    public static Model.Finder<Long, UserEventStats> find = new Model.Finder<Long, UserEventStats>(Long.class, UserEventStats.class);

    public static List<UserEventStats> findAllUserEventStats() {
        return find.findList();
    }
}
