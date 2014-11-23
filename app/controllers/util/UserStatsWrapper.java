package controllers.util;
import models.User;
import models.UserEventStats;
import models.UserStats;

import java.util.List;

/**
 * Created by Supriya on 22/11/2014.
 */
public class UserStatsWrapper {
    public User user;

    public UserEventStatsWrapper userEventStatsWrapper;

    public int noOfEventsParticipatedIn;

    public int noOfIndividualInformalMessages;

    public int noOfIndividualHashTagMessages;

    public double cognitiveAbilitiesScore;

    public int upVotes;

    public UserStatsWrapper(User user,UserEventStatsWrapper userEventStatsWrapper){
        this.user = user;
        this.userEventStatsWrapper = userEventStatsWrapper;
    }

    public void userStatsUpdate(UserEventStatsWrapper userEventStatsWrapper){
        UserStats userStats = UserStats.findUserStatsByUser(user);
        if(userStats != null) {
            userStats.noOfEventsParticipatedIn++;
            userStats.noOfIndividualInformalMessages = userStats.noOfIndividualInformalMessages + userEventStatsWrapper.noOfIndividualInformalMessagesInEvent;
            userStats.noOfIndividualHashTagMessages = userStats.noOfIndividualHashTagMessages + userEventStatsWrapper.getNoOfIndividualHashTagMessagesInEvent;
            //[TODO] Logic For upvotes and conginitive abilities score
            userStats.upVotes = 0;
            userStats.cognitiveAbilitiesScore = 0;
            userStats.update();
        } else{
            System.out.println("User does not exist in the system");
        }
    }
}
