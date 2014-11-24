package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
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

    //aggregate score
    //public int aggregateScore

    //percentage of his individual discussion

    @Formats.NonEmpty
    public float cognitiveAbilitiesScore;

    @Formats.NonEmpty
    public int upVotes;

    public static Model.Finder<Long, UserStats> find = new Model.Finder<Long, UserStats>(Long.class, UserStats.class);

    public static UserStats findUserStatsByUser(User user){
        return find.where().eq("user", user).findUnique();
    }
}
