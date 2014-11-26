package controllers.util;

import models.*;


import java.util.List;

/**
 * Created by Supriya on 22/11/2014.
 */
public class UserEventStatsWrapper {

    public User user;

    public Event event;

    //public UserStats userStatsWrapper;

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

    //Constructor
    public UserEventStatsWrapper(User user,Event event){
        this.user = user;
        this.event = event;
        this.phase1AnswerInEvent = false;
        this.phase3AnswerInEvent = false;
        this.phase4AnswerInEvent = false;
    }

    public void userEventAggregator(EventStatsWrapper eventStatsWrapper){
        this.userAnswerScoreAggregator();
        this.cognitiveAbilityScoreCalculator();
        this.userMessageAggragator(eventStatsWrapper);
        this.userCollaborativeIndex();
        this.upVoteCalculator();
        this.userEventStatsSave();
        this.userStatsUpdate(eventStatsWrapper);
    }

    public void userAnswerScoreAggregator(){
        if(this.phase1AnswerInEvent) this.scorePhase1InEvent = 35; else this.scorePhase1InEvent = 0;
        if(this.phase3AnswerInEvent) this.scorePhase3InEvent = 25; else this.scorePhase3InEvent = 0;
        if(this.phase4AnswerInEvent) this.scorePhase4InEvent = 40; else this.scorePhase4InEvent = 0;
        this.aggregatedScoreForEvent = this.scorePhase1InEvent + this.scorePhase3InEvent + this.scorePhase4InEvent;
        System.out.println("The aggregated score of the user for the event is:"+ this.aggregatedScoreForEvent);
    }

    public void cognitiveAbilityScoreCalculator(){
        //Initialize
        this.cognitiveAbilityScore = 0;
        this.percentageOfCognitiveAbility = 0;
        //Got it wrong in phase 1 and 4
        if(!(this.phase1AnswerInEvent && this.phase4AnswerInEvent)){
            this.cognitiveAbilityScore = 0;
            this.percentageOfCognitiveAbility = 0;
        }
        //Got it right in phase 1 and wrong in phase 4
        if(this.phase1AnswerInEvent && !(this.phase4AnswerInEvent)){
            this.cognitiveAbilityScore = 1;
            this.percentageOfCognitiveAbility = 50;
        }
        //Got it wrong in phase 1 and right in phase 4
        if(this.phase1AnswerInEvent && !(this.phase4AnswerInEvent)){
            this.cognitiveAbilityScore = 1;
            this.percentageOfCognitiveAbility = 50;
        }
        //Got it right in both phases
        if(this.phase1AnswerInEvent && this.phase4AnswerInEvent){
           this.cognitiveAbilityScore = 2;
           this.percentageOfCognitiveAbility = 100;
       }
    }

    public void userMessageAggragator(EventStatsWrapper e) {
        int noOfMsgs,totalMsgs;
        this.percentageContributionForDiscussionInEvent = 0;
        this.noOfIndividualHashTagMessagesInEvent = EventActions.findHashTagMessagesByEventIDUserID(this.user, this.event).size();
        noOfMsgs = EventActions.findMessagesByEventIDUserID(this.user,this.event).size();
        this.noOfIndividualInformalMessagesInEvent = noOfMsgs - this.noOfIndividualHashTagMessagesInEvent;
        totalMsgs = e.noOfHashTagMessgaes+e.noOfInformalMessages;
        if(totalMsgs != 0) {
            this.percentageContributionForDiscussionInEvent = ((noOfMsgs * 1.0) / (totalMsgs * 1.0)) * 100;
        }
    }

    public void userCollaborativeIndex(){
        this.collaborativeIndexForEvent = this.percentageContributionForDiscussionInEvent/100;
    }

    public void upVoteCalculator(){
        int noOfUpVotesRecieved = 0;
        noOfUpVotesRecieved = EventActions.findAllUpvotesForUserByEventID(this.user,this.event).size();
        System.out.println("This was the noOfUpvotesSizeList----->"+ noOfUpVotesRecieved);
        this.noOfUpVotesReceivedForEvent = noOfUpVotesRecieved;
    }
    public void userEventStatsSave(){
        UserEventStats userEventStats = new UserEventStats();
        userEventStats.user = this.user;
        userEventStats.event = this.event;
        userEventStats.phase1AnswerInEvent = this.phase1AnswerInEvent;
        userEventStats.phase3AnswerInEvent = this.phase3AnswerInEvent;
        userEventStats.phase4AnswerInEvent = this.phase4AnswerInEvent;
        userEventStats.noOfIndividualHashTagMessagesInEvent = this.noOfIndividualHashTagMessagesInEvent;
        userEventStats.noOfIndividualInformalMessagesInEvent = this.noOfIndividualInformalMessagesInEvent;
        userEventStats.percentageContributionForDiscussionInEvent = this.percentageContributionForDiscussionInEvent;
        userEventStats.scorePhase1InEvent = this.scorePhase1InEvent;
        userEventStats.scorePhase3InEvent = this.scorePhase3InEvent;
        userEventStats.scorePhase4InEvent = this.scorePhase4InEvent;
        userEventStats.aggregatedScoreForEvent = this.aggregatedScoreForEvent;
        userEventStats.cognitiveAbilityScore = this.cognitiveAbilityScore;
        userEventStats.percentageOfCognitiveAbility = this.percentageOfCognitiveAbility;
        userEventStats.collaborativeIndexForEvent = this.collaborativeIndexForEvent;
        userEventStats.noOfUpVotesReceivedForEvent = this.noOfUpVotesReceivedForEvent;
//        System.out.println("USer ID and :"+this.user.id + "|" + this.event.eventId);
//        System.out.println("Phase 1,3 and 4 in event:"+ this.phase1AnswerInEvent + "|"+ this.phase3AnswerInEvent + "|" + this.phase4AnswerInEvent);
//        System.out.println("No Of induvidual hash and informal msgs:"+ this.noOfIndividualHashTagMessagesInEvent + "|" + this.noOfIndividualInformalMessagesInEvent);
//        System.out.println("percentage:"+this.percentageContributionForDiscussionInEvent);
//        System.out.println("scores in phase event 1,3,4 and aggregated score:" + this.scorePhase1InEvent + "|" + this.scorePhase3InEvent + "|" + this.scorePhase4InEvent + "|" + this.aggregatedScoreForEvent);
//        System.out.println("cognitive ability score "+ this.cognitiveAbilityScore + " percentageOfcognitveability" + this.percentageOfCognitiveAbility);
//        System.out.println("collaborative index:" + this.collaborativeIndexForEvent + " noOfUpVotesRecieved:" + this.noOfUpVotesReceivedForEvent);
//        System.out.println("JUST BEFORE SAVING");
        userEventStats.save();
    }

    public void userStatsUpdate(EventStatsWrapper eventStatsWrapper){
        UserStatsWrapper userStatsWrapper = new UserStatsWrapper(user,this);
        userStatsWrapper.userStatsUpdate(this,eventStatsWrapper);
    }
}
