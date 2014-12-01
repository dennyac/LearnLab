package controllers;

import akka.InstructorWebSocket;
import akka.UserWebSocket;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;
import views.html.exceptionLandingPage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static play.data.Form.form;

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

//        Form<EventStats> userForm = form(EventStats.class);
//        EventStats s = userForm.bindFromRequest().get();
//        System.out.println("Came HERE!!"+ s.answer);
        try {
            User currUser = User.findByEmail(request().username());
            Event eventSelected = Event.findById(eventId);
            Question question = eventSelected.Questions.get(0);
            String[] hashTagsFromEvent = eventSelected.getHashTags();
            List<String> hashTags = Arrays.asList(hashTagsFromEvent);
//        System.out.println("Request().username:"+ currUser.fullname);
//        System.out.println("User email id is " + currUser.email);
            return ok(chatRoom.render((currUser), eventSelected, hashTags, question));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong while entering the chat room"));
        }
    }

    public static Result chatRoomJs(String username, long eventId) {
        return ok(views.js.chatRoom.render(username,eventId));
    }




    /**
     * Handle the chat websocket.
//     */

    //chatRoom.scala.js should handle the error if the websocket connection fails
    //Or render some error html pages
    public static WebSocket<JsonNode> chat(final String username, final long eventId) {

            return WebSocket.withActor(new F.Function<ActorRef, Props>() {
                public Props apply(ActorRef out) throws Throwable {
                    return UserWebSocket.props(username, eventId, out);
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

    public static Result instructorJs(String username) {
//        List<String> hashtags = new ArrayList<String>();
//        hashtags.add("Informal");
//        hashtags.add("Formal");
//        hashtags.add("#justify");
//        hashtags.add("#explain");
//        hashtags.add("#example");
        List<String> hashtags =Event.getAllHashTags();
        StringBuilder output = new StringBuilder();
        output.append("[");
        for(int i=0;i<hashtags.size();i++){
            output.append("{");
            output.append("\"x\":");
            output.append("\"");
            output.append(hashtags.get(i));
            output.append("\"");
            output.append(",");
            output.append("\"y\":");
            output.append("\"");
            output.append(String.valueOf(0));
            output.append("\"");
            output.append("},");
        }
        String response = output.toString();
        response = response.substring(0, response.length()-1);
        response = response + "]";
        return ok(views.js.instructorView.render(username, response, hashtags));
    }
}
