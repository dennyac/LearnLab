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

    public int getNoOfIndividualHashTagMessagesInEvent;

    public double scorePhase1InEvent;

    public double scorePhase3InEvent;

    public double scorePhase4InEvent;

    public double aggregatedScoreForEvent;

    public double collaborativeIndexForEvent;

    //Constructor
    public UserEventStatsWrapper(User user,Event event){
        this.user = user;
        this.event = event;
        this.phase1AnswerInEvent = false;
        this.phase3AnswerInEvent = false;
        this.phase4AnswerInEvent = false;
    }

    public void userEventAggregator(){
        this.userAnswerScoreAggregator();
        this.userMessageAggragator();
        this.userCollaborativeIndex();
        this.userEventStatsSave();
        this.userStatsUpdate();
    }

    public void userAnswerScoreAggregator(){
        if(this.phase1AnswerInEvent) this.scorePhase1InEvent = 35;
        if(this.phase3AnswerInEvent) this.scorePhase3InEvent = 25;
        if(this.phase4AnswerInEvent) this.scorePhase4InEvent = 40;
        this.aggregatedScoreForEvent = this.scorePhase1InEvent + this.scorePhase3InEvent + this.scorePhase4InEvent;
        System.out.println("The aggregated score of the user for the event is:"+ this.aggregatedScoreForEvent);
    }

    public void userMessageAggragator() {
        int noOfMsgs;
        this.getNoOfIndividualHashTagMessagesInEvent = EventActions.findHashTagMessagesByEventIDUserID(this.user, this.event).size();
        noOfMsgs = EventActions.findMessagesByEventIDUserID(this.user,this.event).size();
        this.noOfIndividualInformalMessagesInEvent = noOfMsgs - this.getNoOfIndividualHashTagMessagesInEvent;
    }

    public void userCollaborativeIndex(){
        //[TODO]: Write Logic for this
        this.collaborativeIndexForEvent = 0.0;
    }

    public void userEventStatsSave(){
        UserEventStats userEventStats = new UserEventStats();
        userEventStats.user = this.user;
        userEventStats.event = this.event;
        userEventStats.phase1AnswerInEvent = this.phase1AnswerInEvent;
        userEventStats.phase3AnswerInEvent = this.phase3AnswerInEvent;
        userEventStats.phase4AnswerInEvent = this.phase4AnswerInEvent;
        userEventStats.getNoOfIndividualHashTagMessagesInEvent = this.getNoOfIndividualHashTagMessagesInEvent;
        userEventStats.noOfIndividualInformalMessagesInEvent = this.noOfIndividualInformalMessagesInEvent;
        userEventStats.scorePhase1InEvent = this.scorePhase1InEvent;
        userEventStats.scorePhase3InEvent = this.scorePhase3InEvent;
        userEventStats.scorePhase4InEvent = this.scorePhase4InEvent;
        userEventStats.aggregatedScoreForEvent = this.aggregatedScoreForEvent;
        userEventStats.collaborativeIndexForEvent = this.collaborativeIndexForEvent;
        userEventStats.save();
    }

    public void userStatsUpdate(){
        UserStatsWrapper userStatsWrapper = new UserStatsWrapper(user,this);
        userStatsWrapper.userStatsUpdate(this);
    }
}
