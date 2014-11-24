package akka.messages;

import akka.actor.ActorRef;

/**
 * Created by dennyac on 11/23/14.
 */
public class Quit {

    private final String username;
    private final ActorRef outSocket;

    public String getUsername() {
        return username;
    }

    public String getType() {
        return "quit";
    }

    public ActorRef getOutSocket() {
        return outSocket;
    }

    public Quit(String username, ActorRef outSocket) {
        this.username = username;
        this.outSocket = outSocket;
    }

}