package models;

/**
 * Created by shruthir28 on 11/26/2014.
 */
public class ReportAnalytics {

    public String getMaxPercentPhase() {
        return maxPercentPhase;
    }

    public void setMaxPercentPhase(String maxPercentPhase) {
        this.maxPercentPhase = maxPercentPhase;
    }

    String maxPercentPhase;

    public String getHashMsgs() {
        return hashMsgs;
    }

    public void setHashMsgs(String hashMsgs) {
        this.hashMsgs = hashMsgs;
    }

    String hashMsgs;

    String collabMsg;

    public String getCollabMsg() {
        return collabMsg;
    }

    public void setCollabMsg(String collabMsg) {
        this.collabMsg = collabMsg;
    }

    public void analyze(Event e)
    {
        EventStats stat = e.eventStats;
        double phase1 = stat.percentageCorrectInPhase1;
        double phase3 = stat.percentageCorrectInPhase3;
        double phase4 = stat.percentageCorrectInPhase4;

        //Analytics on answers from different phases of the event
        if(stat == null)
        {
            System.out.println("the event stats does not exist");
            return;
        }
        double max = Math.max(phase1, Math.max(phase3,phase4));
        if(max==phase1)
        {
            this.setMaxPercentPhase("Majority of the people got the question right in phase 1");
        }
        else if(max==phase3)
        {
            this.setMaxPercentPhase("Majority of the people got the question right in phase 3");
        }
        else if(max == phase4)
        {
            this.setMaxPercentPhase("Majority of the people got the question right in phase 4");
        }
        else
        {
            System.out.println("invalid max");
        }

        //Inferences on the number of formal and informal messages
        if(stat.noOfInformalMessages> stat.noOfInformalMessages)
        {
            this.setHashMsgs("The group chat had majority of informal messages that encouraged students to interact");
        }
        else if(stat.noOfInformalMessages < stat.noOfInformalMessages)
        {
            this.setHashMsgs("The group chat had majority of formal messages ");
        }
        else
        {
            this.setHashMsgs("The group chat had equal number of formal and informal messages ");
        }

        if(stat.positiveCollaborationScore > 0.6)
        {
            this.setCollabMsg("Most of the participants who got the answer wrong initially changed their answers to the right one after colloboration in the group chat ");
        }

        if(stat.negativeCollaborationScore > 0.6)
        {
            this.setCollabMsg("Most of the participants who got the answer right initially changed their answers to the wrong one after colloborating in the group chat");
        }



    }
}
