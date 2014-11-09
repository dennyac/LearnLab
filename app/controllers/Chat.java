package controllers;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import models.ChatRoomManager;
import models.Instructor;
import models.InstructorManager;
import models.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;
import views.html.index;
import akka.actor.*;

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
    public static Result chatRoom() {
//        if(username == null || username.trim().equals("")) {
//            flash("error", "Please choose a valid username.");
//            return redirect(routes.Application.index());
//        }

        return ok(chatRoom.render(User.findByEmail(request().username())));
    }

    public static Result chatRoomJs(String username) {
        return ok(views.js.chatRoom.render(username));
    }

    /**
     * Handle the chat websocket.
     */
    public static WebSocket<JsonNode> chat(final String username) {
        return new WebSocket<JsonNode>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                
                // Join the chat room.
                try { 
                    ChatRoomManager.joinChatRoom(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    public static WebSocket<JsonNode> instructor(final String username) {
        return new WebSocket<JsonNode>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){

                // Join the chat room.
                try {
                    InstructorManager.createInstructor(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

//    public static WebSocket<String> socket() {
//        return WebSocket.withActor(new F.Function<ActorRef, Props>() {
//            public Props apply(ActorRef out) throws Throwable {
//                return Instructor.props(out);
//            }
//        });
//    }
  
}
