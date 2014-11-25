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

    public int noOfMessagesExchangedByAllInAllEventsUserParticipated;

    public double percentageContributionForDiscussion;

    public double aggregateScore;

    public int cognitiveAbilitiesScore;

    public double percentageOfCognitiveAbility;

    public int upVotes;

    public UserStatsWrapper(User user,UserEventStatsWrapper userEventStatsWrapper){
        this.user = user;
        this.userEventStatsWrapper = userEventStatsWrapper;
    }

    public double getPercentageContributionForDiscussion(int noOfMessagesExchangedByAllInAllEventsUserParticipated,int totalMsgs){
        double percentageContribution = ((totalMsgs*1.0)/(noOfMessagesExchangedByAllInAllEventsUserParticipated*1.0))*100;
        return percentageContribution;
    }

    public double getPercentageOfCognitiveAbility(int eventsParticipated, int cognitiveScore){
            // the no of answers he got right in phase 1 and 4 divided by the total number of cognitive questions peresent.
           double percentageOfCognitiveAbility = ((cognitiveScore*1.0)/(eventsParticipated*2.0))*100;
           return percentageOfCognitiveAbility;
    }

    public void userStatsUpdate(UserEventStatsWrapper userEventStatsWrapper,EventStatsWrapper eventStatsWrapper){
        UserStats userStats = UserStats.findUserStatsByUser(user);
        if(userStats != null) {
            userStats.noOfEventsParticipatedIn++;
            userStats.noOfIndividualInformalMessages = userStats.noOfIndividualInformalMessages + userEventStatsWrapper.noOfIndividualInformalMessagesInEvent;
            userStats.noOfIndividualHashTagMessages = userStats.noOfIndividualHashTagMessages + userEventStatsWrapper.noOfIndividualHashTagMessagesInEvent;
            userStats.noOfMessagesExchangedByAllInAllEventsUserParticipated = userStats.noOfMessagesExchangedByAllInAllEventsUserParticipated + eventStatsWrapper.totalNoOfMessages;
            userStats.percentageContributionForDiscussion = getPercentageContributionForDiscussion
                                                                (userStats.noOfMessagesExchangedByAllInAllEventsUserParticipated,
                                                                        ((userStats.noOfIndividualInformalMessages)+
                                                                           (userStats.noOfIndividualHashTagMessages)));
            userStats.cognitiveAbilitiesScore = userStats.cognitiveAbilitiesScore + userEventStatsWrapper.cognitiveAbilityScore;
            userStats.percentageOfCognitiveAbility = getPercentageOfCognitiveAbility(userStats.noOfEventsParticipatedIn,userStats.cognitiveAbilitiesScore);
            userStats.upVotes = userStats.upVotes + userEventStatsWrapper.noOfUpVotesReceivedForEvent;
            userStats.aggregateScore = userStats.aggregateScore + userEventStatsWrapper.aggregatedScoreForEvent;
            userStats.update();
        } else{
            System.out.println("User does not exist in the system");
        }
    }
}
