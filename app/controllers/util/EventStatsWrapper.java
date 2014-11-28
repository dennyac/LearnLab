package controllers.util;

import models.Event;
import models.EventActions;
import models.EventStats;
import models.User;

import java.util.*;

/**
 * Created by Supriya on 22/11/2014.
 */
public class EventStatsWrapper {

    public Event event;

    public Long eventId;

    public int noOfPraticipants;

    public int totalNoOfMessages;

    public int noOfHashTagMessgaes;

    public int noOfInformalMessages;

    public double percentageCorrectInPhase1;

    public double percentageCorrectInPhase3;

    public double percentageCorrectInPhase4;

    public double positiveCollaborationScore;

    public double negativeCollaborationScore;

    public HashMap<User,List<Boolean>> tempUserAnswerRecorder;

    public EventStatsWrapper(Event event1){
        this.event = event1;
        this.noOfPraticipants = event1.participants.size();
        this.tempUserAnswerRecorder = new HashMap<User,List<Boolean>>();
    }

    public void eventStatsAggregator(){
        System.out.println("In the eventAggregator Function of EventStatsWrapper");
        this.eventMessagesAggregator();
        this.eventCorrectAnswerAggregator();
        this.eventCollaborationScoreAggregator();
        //For persisting the results in the database
        this.eventStatsSave();
    }

    public void  eventMessagesAggregator(){
          this.totalNoOfMessages = EventActions.findAllInformalMessagesOfEvents(event).size();
          this.noOfHashTagMessgaes = EventActions.findHashTagMessagesOfEvents(event).size();
          this.noOfInformalMessages = this.totalNoOfMessages - this.noOfHashTagMessgaes;
//        this.noOfInformalMessages = EventActions.findNoOfInformalMessagesByEvent(this.event);
          System.out.println("Number Of Informal Messages:" + this.noOfInformalMessages);
//        this.noOfHashTagMessgaes = EventActions.findHashTagMessageCountByEvent(this.event);
          System.out.println("Number Of HashTag Messages:" + this.noOfHashTagMessgaes);
//        this.totalNoOfMessages = this.noOfInformalMessages + this.noOfHashTagMessgaes;
    }

    public void eventCorrectAnswerAggregator(){
        int correctInPhase1 = 0;
        int correctInPhase3 = 0;
        int correctInPhase4 = 0;
        System.out.println("Event partcipants are:" + this.event.participants.size());
        for(User user: this.event.participants)
        {
            System.out.println("This is the boolean:"+EventActions.hasAUserParticipatedInAnEvent(this.event,user));
            if(EventActions.hasAUserParticipatedInAnEvent(this.event,user)) {
                //Aggregating Each Users Individual Performance;
                UserEventStatsWrapper userEventStatsWrapper = new UserEventStatsWrapper(user,this.event);
                List<Boolean> answerRecorder = new ArrayList<Boolean>(2);
                answerRecorder.add(false);
                answerRecorder.add(false);
                EventActions eaStage1 = EventActions.findByEventIDUserIDActionType(this.event, user, "Stage1");
                System.out.println("Event Stage aggregator" + eaStage1.id);
                String answer1 = this.event.Questions.get(0).Answer;
                System.out.println("Answer is" + answer1);
                System.out.println("Event Stage aggregator is:" + eaStage1.Attribute1);
                if (eaStage1.Attribute1.equalsIgnoreCase(this.event.Questions.get(0).Answer)) {
                    correctInPhase1++;
                    userEventStatsWrapper.phase1AnswerInEvent = true;
                    answerRecorder.set(0,true);
                }
                EventActions eaStage3 = EventActions.findByEventIDUserIDActionType(this.event, user, "Stage3");
                if (eaStage3.Attribute1.equalsIgnoreCase(this.event.Questions.get(0).Answer)) {
                    correctInPhase3++;
                    userEventStatsWrapper.phase3AnswerInEvent = true;
                    answerRecorder.set(1,true);
                }
                EventActions eaStage4 = EventActions.findByEventIDUserIDActionType(this.event, user, "Stage4");
                if (eaStage4.Attribute1.equalsIgnoreCase(this.event.Questions.get(1).Answer)) {
                    correctInPhase4++;
                    userEventStatsWrapper.phase4AnswerInEvent = true;
                }
                this.tempUserAnswerRecorder.put(user,answerRecorder);
                userEventStatsWrapper.userEventAggregator(this);
            }
        }
        System.out.println(" CorrectInPhase1:"+correctInPhase1+ " CorrectInPhase3:"+ correctInPhase3 + "CorrectInPhase4:"+ correctInPhase4);
        System.out.println("No of participants:"+ this.noOfPraticipants);
        System.out.println("Ans we are getting in phase 1 is:" + ((correctInPhase1 * 1.0)) / (this.noOfPraticipants * 1.0));
        this.percentageCorrectInPhase1 = ((correctInPhase1*1.0)/(this.noOfPraticipants*1.0))*100;
        this.percentageCorrectInPhase3 = ((correctInPhase3*1.0)/(this.noOfPraticipants*1.0))*100;
        this.percentageCorrectInPhase4 = ((correctInPhase4*1.0)/(this.noOfPraticipants*1.0))*100;
        System.out.println("Percetages in 1 3 4 are:"+ this.percentageCorrectInPhase1 + ","+ this.percentageCorrectInPhase3 + ","+ this.percentageCorrectInPhase4);
    }

    public void eventCollaborationScoreAggregator(){
        //Local Variables to calculate the collaboration score
        int noWhoGotItRightInPhase1 = 0;
        int noWhoGotItWrongInPhase1 = 0;
        int noWhoGotItRightInPhase1WrongInPhase3 = 0;
        int noWhoGotItWrongInPhase1RightInPhase3 = 0;
        this.positiveCollaborationScore = 0;
        this.negativeCollaborationScore = 0;
        for(Map.Entry<User, List<Boolean>> entry : this.tempUserAnswerRecorder.entrySet()){
            List<Boolean> answers= entry.getValue();
            if(answers.get(0) == false){
                noWhoGotItWrongInPhase1++;
                        if(answers.get(1) == true){
                            noWhoGotItWrongInPhase1RightInPhase3++;
                        }
            }else{
                //who got it right in phase1
                noWhoGotItRightInPhase1++;
                if(answers.get(1)== false){
                    noWhoGotItRightInPhase1WrongInPhase3++;
                }
            }
        }
        if(noWhoGotItWrongInPhase1 != 0) {
            System.out.println("Wrong in Phase1 and Right in phase 3");
            this.positiveCollaborationScore = (noWhoGotItWrongInPhase1RightInPhase3 * 1.0) / (noWhoGotItWrongInPhase1 * 1.0);
        } else{
            this.positiveCollaborationScore = 0;
        }
        if(noWhoGotItRightInPhase1 != 0){
            System.out.println("Right in Phase1 and Wrong in phase 3");
            this.negativeCollaborationScore = (noWhoGotItRightInPhase1WrongInPhase3*1.0)/(noWhoGotItRightInPhase1*1.0);
        } else{
            this.negativeCollaborationScore = 0;
        }

    }

    public void eventStatsSave(){
        EventStats eventStats = new EventStats();
        eventStats.event = this.event;
        eventStats.eventId = this.event.eventId;
        eventStats.noOfPraticipants = this.noOfPraticipants;
        eventStats.noOfHashTagMessgaes = this.noOfHashTagMessgaes;
        eventStats.noOfInformalMessages = this.noOfInformalMessages;
        eventStats.totalNoOfMessages = this.totalNoOfMessages;
        eventStats.percentageCorrectInPhase1 = this.percentageCorrectInPhase1;
        eventStats.percentageCorrectInPhase3 = this.percentageCorrectInPhase3;
        eventStats.percentageCorrectInPhase4 = this.percentageCorrectInPhase4;
        eventStats.positiveCollaborationScore = this.positiveCollaborationScore;
        eventStats.negativeCollaborationScore = this.negativeCollaborationScore;
        eventStats.save();
    }
}

