package models;
import com.avaje.ebean.Ebean;
import org.joda.time.Minutes;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.utils.AppException;
import models.utils.Hash;
import models.Event;
import models.User;
import models.EventStats;
import models.UserStats;
import models.UserEventStats;
import models.EventActions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

public class ReportAnalytics {

    String maxPercentPhase;
    String hashMsgs;
    String collabMsg;

    public String getMaxPercentPhase() {
        return maxPercentPhase;
    }

    public void setMaxPercentPhase(String maxPercentPhase) {
        this.maxPercentPhase = maxPercentPhase;
    }

    public String getHashMsgs() {
        return hashMsgs;
    }

    public void setHashMsgs(String hashMsgs) {
        this.hashMsgs = hashMsgs;
    }

    public String getCollabMsg() {
        return collabMsg;
    }

    public void setCollabMsg(String collabMsg) {
        this.collabMsg = collabMsg;
    }

    public void analyze(String eventName) {
        System.out.println("Inside report analyze function");
        Event e=Event.findByName(eventName);
        System.out.println("creation of event object successful "+e.eventName);
        //Check if the event is successfully completed
        if(e.active==2) {
            //Find event stats by event id
            EventStats stat = EventStats.findByEventName(eventName);
            //EventStats stat = EventStats.findByEvent(e);
            System.out.println("creation of stats object successful " + stat.id);
            double phase1 = stat.percentageCorrectInPhase1;
            double phase3 = stat.percentageCorrectInPhase3;
            double phase4 = stat.percentageCorrectInPhase4;

            //Analytics on answers from different phases of the event
            if (stat == null) {
                System.out.println("the event stats does not exist");
                return;
            }

            //MAX PERCENT PHASE
            double max = Math.max(phase1, Math.max(phase3, phase4));
            if (max == phase1) {
                this.setMaxPercentPhase("Majority of the people got the question right in phase 1");
            } else if (max == phase3) {
                this.setMaxPercentPhase("Majority of the people got the question right in phase 3");
            } else if (max == phase4) {
                this.setMaxPercentPhase("Majority of the people got the question right in phase 4");
            } else {
                System.out.println("invalid max");
            }

            // SET HASH TAG MESSAGES Inferences on the number of formal and informal messages
            if (stat.noOfInformalMessages > stat.noOfHashTagMessgaes) {
                this.setHashMsgs("The group chat had majority of informal messages that encouraged students to interact");
            } else if (stat.noOfInformalMessages < stat.noOfHashTagMessgaes) {
                this.setHashMsgs("The group chat had majority of formal messages ");
            } else {
                this.setHashMsgs("The group chat had equal number of formal and informal messages ");
            }

            //COLLABORATION MESSAGES
            if (stat.positiveCollaborationScore > 0.6) {
                this.setCollabMsg("Most of the participants who got the answer wrong initially changed their answers to the right one after colloboration in the group chat ");
            }
            else if (stat.negativeCollaborationScore > 0.6) {
                this.setCollabMsg("Most of the participants who got the answer right initially changed their answers to the wrong one after colloborating in the group chat");
            }
            else
            {
                this.setCollabMsg("Participants did not benefit from collaboration");
            }
        }
        else
        {
            System.out.println("The event is not completed");
        }
    }
}
