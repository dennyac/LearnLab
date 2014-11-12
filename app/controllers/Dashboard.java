package controllers;

import models.Event;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;
import views.html.dashboard.instructorDashboard;
import views.html.dashboard.manageEvents;
import views.html.dashboard.createEventConfirmation;
import views.html.dashboard.deleteEventConfirmation;
import views.html.dashboard.updateEventConfirmation;
import views.html.chatRoom;
import views.html.instructorView;
import java.util.*;

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

    public static Result manageEvents()
    {
        return ok(manageEvents.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result createEvent(String arg)
    {
        System.out.println("argument is "+ arg);
        Event eventObj=new Event();
        String[] createFormData=arg.split("\\|");

        //Need to make sure User is in the db already
        //Participant
        System.out.println("This is the first element"+createFormData[0]+ "second"+createFormData[1]);

        User u=User.findByFullname(createFormData[1]);
        System.out.println("user "+ u.fullname);

        if(u==null)
            System.out.println("User is null");

        if(eventObj.participants==null)
            eventObj.participants=new ArrayList<User>();
        else
            eventObj.participants.add(u);

        //event name
        eventObj.eventName=createFormData[2];
        System.out.println("argument is "+ eventObj.eventName);

        if(eventObj.hashTags==null)
            eventObj.hashTags=new HashSet<String>();
        else
            eventObj.hashTags.add(createFormData[3]);






        return ok(createEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result deleteEvent()
    {
        return ok(deleteEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result updateEvent()
    {
        return ok(updateEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result instructorView() {
//        if(username == null || username.trim().equals("")) {
//            flash("error", "Please choose a valid username.");
//            return redirect(routes.Application.index());
//        }
        User currUser = User.findByEmail(request().username());
        System.out.println("Request().username:"+ currUser.fullname);
        System.out.println("User email id is " + currUser.email);
        return ok(instructorView.render(currUser));
    }

}
//        public static Result index() {
//        return START_CHAT;
//    }

