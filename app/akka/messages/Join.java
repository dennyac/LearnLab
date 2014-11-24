package akka.messages;

import akka.actor.ActorRef;

/**
 * Created by dennyac on 11/23/14.
 */
public class Join {

    private final String username;
    private final ActorRef outSocket;
    private final String message;

    public String getUsername() {
        return username;
    }

    public String getType() {
        return "join";
    }

    public String getMessage() {
        return message;
    }

    public ActorRef getOutSocket() {
        return outSocket;
    }

    public Join(String username, ActorRef outSocket, String message) {
        this.username = username;
        this.outSocket = outSocket;
        this.message = message;
    }
}
