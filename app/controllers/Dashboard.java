package controllers;

import models.Event;
import models.Question;
import models.User;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.data.Form;
import views.html.dashboard.index;
import views.html.dashboard.instructorDashboard;
import views.html.dashboard.manageEvents;
import views.html.dashboard.createEventConfirmation;
import views.html.dashboard.deleteEventConfirmation;
import views.html.dashboard.updateEventConfirmation;
import views.html.chatRoom;
import views.html.eventSequence.eventStage1;
import views.html.instructorView;
import java.util.List;
import java.util.Set;
import scala.collection.JavaConverters;

import static play.data.Form.form;


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
        else {
            List<Event> eventList = currentUser.EventsParticipated;
            return ok(index.render((User.findByEmail(request().username())), eventList));
        }
     }

    public static Result manageEvents()
    {
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findEvent();
        return ok(manageEvents.render(currentUser,eventSelected,form(CreateEventForm.class)));
    }

    public static Result createEvent() {
        Form<CreateEventForm> form2 = form(CreateEventForm.class);
        CreateEventForm f = form2.bindFromRequest().get();
        System.out.println("Read event form!!"+ f.eventName);

        //initEventStage(f);

        return ok(createEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result deleteEvent(){
        return ok(deleteEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result updateEvent(){
        return ok(updateEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result instructorView() {
//        if(username == null || username.trim().equals("")) {
//            flash("error", "Please choose a valid username.");
//            return redirect(routes.Application.index());
//        }
        User currUser = User.findByEmail(request().username());
//        System.out.println("Request().username:"+ currUser.fullname);
//        System.out.println("User email id is " + currUser.email);
        return ok(instructorView.render(currUser));
    }

    public static class CreateEventForm {

        @Constraints.Required
        public String eventName;

        @Constraints.Required
        public String eventDescription;

        @Constraints.Required
        public String script1;

        @Constraints.Required
        public String script2;

        @Constraints.Required
        public String script3;

        @Constraints.Required
        public String question;

        @Constraints.Required
        public String option1;

        @Constraints.Required
        public String option2;

        @Constraints.Required
        public String option3;

        @Constraints.Required
        public String option4;

        @Constraints.Required
        public String answer;

        @Constraints.Required
        public String fquestion;

        @Constraints.Required
        public String foption1;

        @Constraints.Required
        public String foption2;

        @Constraints.Required
        public String foption3;

        @Constraints.Required
        public String foption4;

        @Constraints.Required
        public String fanswer;

        @Constraints.Required
        public String date;

        @Constraints.Required
        public String endTime;

        @Constraints.Required
        public String startTime;

    }
}
//        public static Result index() {
//        return START_CHAT;
//    }

