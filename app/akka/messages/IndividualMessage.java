package akka.messages;

import akka.actor.ActorRef;

/**
 * Created by dennyac on 11/25/14.
 */
public class IndividualMessage {

    private final String username;
    //final Long eventId;
    private final String text;

    public String getUsername() {
        return username;
    }

    //public Long getEventId() {
//        return eventId;
//    }

    public String getText() {
        return text;
    }

    public String getType() {
        return "talk";
    }


    public IndividualMessage(String username, String text) { //, Long eventId
        //this.eventId = eventId;
        this.username = username;
        this.text = text;
    }

}