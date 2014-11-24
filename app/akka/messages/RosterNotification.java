package akka.messages;

/**
 * Created by dennyac on 11/23/14.
 */
public class RosterNotification {

    private final String username;
    private final String direction;
    private final String message;

    public String getUsername() {
        return username;
    }

    public String getDirection() {
        return direction;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return "rosterNotify";
    }

    public RosterNotification(String username, String direction, String message) {
        this.username = username;
        this.direction = direction;
        this.message = message;
    }
}
