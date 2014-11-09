package controllers;

import models.Event;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;
import views.html.dashboard.instructorDashboard;
import views.html.dashboard.createEvent;
import views.html.dashboard.createEventConfirmation;
import views.html.chatRoom;
/**
 * User: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

//    public static Result START_CHAT = redirect(
//            routes.Chat.chatRoom()
//    );
//public static Result index() {
//    User currUser = User.findByEmail(request().username());
//    System.out.println("User email id is " + currUser.email);
//    return ok(chatRoom.render(currUser));
//}
    public static Result index() {
        User currentUser = User.findByEmail(request().username());
        boolean isInstructor = currentUser.isInstructor;
        if(isInstructor){
            return ok(instructorDashboard.render((User.findByEmail(request().username())), Event.findEvent()));
        }
            //is a student
            return ok(index.render((User.findByEmail(request().username())), Event.findEvent()));
        }

    public static Result createEvent()
    {
        return ok(createEvent.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result createEventConfirmation()
    {
        return ok(createEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }
}
//        public static Result index() {
//        return START_CHAT;
//    }

