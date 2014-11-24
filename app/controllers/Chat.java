package controllers;

import akka.InstructorWebSocket;
import akka.UserWebSocket;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;


public class Chat extends Controller {

//    /**
//     * Display the home page.
//     */
//    public static Result index() {
//        return ok(index.render());
//    }

    /**
     * Display the chat room.
     */
    public static Result chatRoom(long eventId) {
//        if(username == null || username.trim().equals("")) {
//            flash("error", "Please choose a valid username.");
//            return redirect(routes.Application.index());
//        }
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
//        System.out.println("Request().username:"+ currUser.fullname);
//        System.out.println("User email id is " + currUser.email);
        return ok(chatRoom.render((currUser),eventSelected));
    }

    public static Result chatRoomJs(String username, long eventId) {
        return ok(views.js.chatRoom.render(username,eventId));
    }

    public static Result instructorJs(String username) {
        return ok(views.js.instructorView.render(username));
    }


    /**
     * Handle the chat websocket.
//     */

    //chatRoom.scala.js should handle the error if the websocket connection fails
    //Or render some error html pages
    public static WebSocket<JsonNode> chat(final String username, final long eventId) {
        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            public Props apply(ActorRef out) throws Throwable {
                return UserWebSocket.props(username,eventId,out);
            }
        });
    }

    public static WebSocket<JsonNode> instructor(final String username) {
        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
            public Props apply(ActorRef out) throws Throwable {
                return InstructorWebSocket.props(username, out);
            }
        });
    }
  
}
