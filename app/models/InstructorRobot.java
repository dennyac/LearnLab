package models;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by dennyac on 11/8/14.
 */

public class InstructorRobot {

    public InstructorRobot(ActorRef actor) {

        // Create a Fake socket out for the robot that log events to the console.
        WebSocket.Out<JsonNode> robotChannel = new WebSocket.Out<JsonNode>() {

            public void write(JsonNode frame) {
                Logger.of("robot").info(Json.stringify(frame));
            }

            public void close() {}

        };

        // Join the room


        // Make the robot talk every 30 seconds
        Akka.system().scheduler().schedule(
                Duration.create(30, SECONDS),
                Duration.create(30, SECONDS),
                actor,
                new ChatRoom.Talk("Robot", "dummyEvent", "I'm still alive"),
                Akka.system().dispatcher(),
                /** sender **/ null
        );

    }

}
