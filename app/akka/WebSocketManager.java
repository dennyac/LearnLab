package akka;

import akka.actor.*;
import play.Logger;
import play.libs.Akka;


import java.util.HashMap;

/**
 * Created by dennyac on 11/7/14.
 */
public class WebSocketManager {


    //public static ActorSystem system = ActorSystem.create("LearnLab");
    private static final Logger.ALogger logger = Logger.of(WebSocketManager.class);
    private static HashMap<Long, ActorRef> chatRooms = new HashMap<Long, ActorRef>();
    private static HashMap<String, ActorRef> instructors = new HashMap<String, ActorRef>();

    public static ActorRef getChatRoom(final Long eventId, final String instructorEmail) {
        if (!chatRooms.containsKey(eventId))
            chatRooms.put(eventId, Akka.system().actorOf(ChatRoom.props(eventId, instructorEmail), eventId.toString()));
        return chatRooms.get(eventId);
    }

    public static ActorRef getInstructor(final String instructorEmail) {
        if (!instructors.containsKey(instructorEmail))
            instructors.put(instructorEmail, Akka.system().actorOf(Instructor.props(instructorEmail), instructorEmail));
        return instructors.get(instructorEmail);
    }
}
