package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

import play.db.ebean.Model;

/**
 * Created by shruthir28 on 11/10/2014.
 */

@Entity
public class UserStats extends Model{

    @Id
    public Long id;

    @OneToOne
    public User user;

    @OneToMany(mappedBy = "userStats")
    public List<UserEventStats> listOfUserEventStatsInStats;

    @Formats.NonEmpty
    public int noOfEventsParticipatedIn;

    @Formats.NonEmpty
    public int noOfIndividualInformalMessages;

    @Formats.NonEmpty
    public int noOfIndividualHashTagMessages;

    @Formats.NonEmpty
    public int noOfMessagesExchangedByAllInAllEventsUserParticipated;

    @Formats.NonEmpty
    public double percentageContributionForDiscussion;

    @Formats.NonEmpty
    public double aggregateScore;

    @Formats.NonEmpty
    public int cognitiveAbilitiesScore;

    @Formats.NonEmpty
    public double percentageOfCognitiveAbility;

    @Formats.NonEmpty
    public int upVotes;

    public static Model.Finder<Long, UserStats> find = new Model.Finder<Long, UserStats>(Long.class, UserStats.class);

    public static UserStats findUserStatsByUser(User user){
        return find.where().eq("user", user).findUnique();
    }

    public static List<UserStats> findLeaderList() {
         List<UserStats> leaderList = find.orderBy("aggregateScore desc").findList();
        System.out.println("Leader List:");
        for(UserStats u: leaderList){
            System.out.println(u.user.fullname + " Has score:" + u.aggregateScore);
        }
        return leaderList;

    }
}
