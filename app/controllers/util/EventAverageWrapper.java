package controllers.util;

import models.EventStats;

/**
 * Created by Supriya on 30/11/2014.
 */
public class EventAverageWrapper {

    public double avgAggregateScore;

    public double noOfInformalMsgs;

    public double noOfFormalMsgs;

    public double percentageCorrectInPhase1;

    public double percentageCorrectInPhase3;

    public double percentageCorrectInPhase4;

    public EventAverageWrapper(EventStats eventStats) {

        if (eventStats.noOfPraticipants != 0) {
            double scoreTotalInPhase1 = ((eventStats.percentageCorrectInPhase1/100.0)*eventStats.noOfPraticipants)*35;
            double scoreTotalInPhase3 = ((eventStats.percentageCorrectInPhase3/100.0)*eventStats.noOfPraticipants)*25;
            double scoreTotalInPhase4 =  ((eventStats.percentageCorrectInPhase4/100.0)*eventStats.noOfPraticipants)*40;
            this.avgAggregateScore = (scoreTotalInPhase1 + scoreTotalInPhase3 + scoreTotalInPhase4)/
                                            (eventStats.noOfPraticipants);
            this.noOfInformalMsgs = eventStats.noOfInformalMessages;
            this.noOfFormalMsgs = eventStats.noOfHashTagMessgaes;
            this.percentageCorrectInPhase1 = eventStats.percentageCorrectInPhase1;
            this.percentageCorrectInPhase3 = eventStats.percentageCorrectInPhase3;
            this.percentageCorrectInPhase4 = eventStats.percentageCorrectInPhase4;
        }

    }
}
