package controllers;

//import jdk.nashorn.internal.ir.ObjectNode;
import models.Event;
import models.Question;
import models.User;
import models.UserStats;
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
import views.html.dashboard.report;
import views.html.chatRoom;
import views.html.eventSequence.eventStage1;
import views.html.instructorView;
import views.html.pastEvents.pastEventsForInstructors;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import scala.collection.JavaConverters;
import java.net.MalformedURLException;
import models.utils.AppException;
import play.Logger;
import java.util.Date;
import java.util.HashSet;
import static play.data.Form.form;
import java.text.*;


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
            User user = User.findByEmail(request().username());
            UserStats userStats = UserStats.findUserStatsByUser(user);
            return ok(index.render(user, userStats, eventList));
        }
     }

    public static Result manageEvents()
    {
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findEvent();
        return ok(manageEvents.render(currentUser,eventSelected,form(CreateEventForm.class)));
    }
    public static Result listUserJs(String instructorUsername) {
        List<User> userList=User.findAllUsers();
        String output="";
        for(User u: userList)
        {
            System.out.println(output);
            output+=u.fullname+"|";
        }
        return ok(views.js.listUser.render(instructorUsername,output));
    }

    public static Result listEventJs(String instructorUsername) {
        List<Event> eventList=Event.findAllEvents();
        String output="";
        for(Event e: eventList)
        {
            System.out.println(output);
            output+=e.eventName+"|";
        }
        return ok(views.js.listEvent.render(instructorUsername,output));
    }

    public static Result createEvent() {
        User instructor = User.findByEmail(request().username());
        Form<CreateEventForm> form2 = form(CreateEventForm.class);
        CreateEventForm f = form2.bindFromRequest().get();
        System.out.println("Read event form!!" + f.eventName);
        System.out.println("participant 1!!" + f.participant1);
        System.out.println("participant 2!!" + f.participant2);
        System.out.println("participant 3!!" + f.participant3);
        System.out.println("participant 4!!" + f.participant4);
        try
        {
            initEventStage(f,instructor);
        }
        catch (Exception ex)
        {
            System.out.println("Error in saving the event");
            ex.printStackTrace();
        }

        return ok(createEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }
    private static void initEventStage(Dashboard.CreateEventForm createEventForm,User instructorUser) throws AppException,MalformedURLException,ParseException
    {
        Event event = new Event();

        //need to add the instructor
        event.instructor=instructorUser;

        //Adding event name
        event.eventName=createEventForm.eventName;

        //Converting string to date
        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(createEventForm.date);
        event.EventDate=date;

        //Start and end time
        event.startTime=createEventForm.startTime;
        event.endTime=createEventForm.endTime;

        //Scripts for all stages
        event.scriptPhase1=createEventForm.script1;
        event.scriptPhase2=createEventForm.script2;
        event.scriptPhase3=createEventForm.script3;

        //Split the event descriptions
        event.hashes=createEventForm.eventDescription;
        /*String[] hashtags=createEventForm.eventDescription.split("#");
        event.hashes=new ArrayList<String>();
        for(String s: hashtags)
        {
            event.hashes.add(s);
            System.out.println("hashtag :"+ s);
        }*/

        //Add participants
        event.participants=new ArrayList<User>();
        User p = User.findByFullname(createEventForm.participant1);
        event.participants.add(p);
        p=User.findByFullname(createEventForm.participant2);
        event.participants.add(p);
        p=User.findByFullname(createEventForm.participant3);
        event.participants.add(p);
        p=User.findByFullname(createEventForm.participant4);
        event.participants.add(p);
        System.out.println("full name "+p.fullname);

        //create question objects
        event.Questions=new ArrayList<Question>();
        Question q=new Question();
        q.questionString=createEventForm.question;
        q.Option1=createEventForm.option1;
        q.Option2=createEventForm.option2;
        q.Option3=createEventForm.option3;
        q.Option4=createEventForm.option4;
        q.Answer=createEventForm.answer;
        q.save();
        event.Questions.add(q);

        Question fq=new Question();
        fq.questionString=createEventForm.fquestion;
        fq.Option1=createEventForm.foption1;
        fq.Option2=createEventForm.foption2;
        fq.Option3=createEventForm.foption3;
        fq.Option4=createEventForm.foption4;
        fq.Answer=createEventForm.fanswer;
        fq.save();
        event.Questions.add(fq);

        //add durations for all the phases
        event.phase1Duration=15L;
        event.phase2Duration=15L;
        event.phase3Duration=15L;

        //save the event
        event.save();

    }
    public static Result deleteEvent(){
        return ok(deleteEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result updateEvent(){
        return ok(updateEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result getEventstoActivate(){
        String s = "[{\"eventId\":\"jhhj\",\"eventName\":\"hjkghsjk\",\"Date\":\"jkdghs jkhsg\"}]";
        return ok(s);
    }

    //public static Result generateReports(String name)
    public static Result generateReports()
    {
        User reportUser = User.findByEmail(request().username());
       // Event reportEvent = Event.findByName(name);
        Event reportEvent = Event.findEvent();
        return ok(report.render(reportUser, reportEvent));
    }

    public static Result activateEvent(String eventId){
        return ok(eventId+" activated");
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

    public static Result instructorPastEvents()
    {
        List<Event> completedEvents = Event.findAllCompletedEvents();
        User reportUser = User.findByEmail(request().username());
        return ok(pastEventsForInstructors.render(reportUser,completedEvents));
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

        @Constraints.Required
        public String participant1;

        @Constraints.Required
        public String participant2;

        @Constraints.Required
        public String participant3;

        @Constraints.Required
        public String participant4;

    }
}
//        public static Result index() {
//        return START_CHAT;
//    }

