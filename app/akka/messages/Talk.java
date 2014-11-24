package akka.messages;

/**
 * Created by dennyac on 11/23/14.
 */
public class Talk {

    final String username;
    final Long eventId;
    final String text;

    public String getUsername() {
        return username;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return "talk";
    }

    public Talk(String username, Long eventId, String text) {
        this.eventId = eventId;
        this.username = username;
        this.text = text;
    }

}