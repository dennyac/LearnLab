package controllers;

//import jdk.nashorn.internal.ir.ObjectNode;
import models.Event;
import models.Question;
import models.User;
import models.UserStats;
import models.UserEventStats;
import models.ReportAnalytics;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.util.Collections;
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

@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

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

    public static Result manageEvents() {
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findEvent();
        return ok(manageEvents.render(currentUser,eventSelected,form(CreateEventForm.class),User.findAllUsers(),Event.findAllEvents()));
    }
    public static Result listUserJs(String divID) {
        List<User> userList=User.findAllUsers();
        String output="";
        for(User u: userList)
        {
            System.out.println(output);
            output+=u.fullname+"|";
        }
        return ok(views.js.listUser.render(divID,output));
    }

    public static Result listEventJs(String divID) {
        List<Event> eventList=Event.findAllEvents();
        String output="";
        for(Event e: eventList)
        {
            System.out.println(output);
            output+=e.eventName+"|";
        }
        return ok(views.js.listEvent.render(divID,output));
    }

    public static Result createEvent() {
        User instructor = User.findByEmail(request().username());
        Form<CreateEventForm> form2 = form(CreateEventForm.class);
        CreateEventForm f = form2.bindFromRequest().get();
        System.out.println("Read event form!!" + f.eventName);
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
    private static void initEventStage(Dashboard.CreateEventForm createEventForm,User instructorUser) throws AppException,MalformedURLException,ParseException {
        Event event = new Event();

        //need to add the instructor
        event.instructor=instructorUser;

        //Adding event name
        event.eventName=createEventForm.eventName;

        //Converting string to date
        //todo form changes
        //Date date = new SimpleDateFormat("MM/dd/yyyy").parse(createEventForm.date);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mmaa");
        event.eventDateTime= formatter.parseDateTime(createEventForm.date + " " +  createEventForm.startTime);

        //Start and end time
        event.startTime=createEventForm.startTime;
        //event.endTime=createEventForm.endTime;

        //Event Description
        event.description=createEventForm.eventDescription;
        System.out.println("hashtag :"+ event.description);
        //Scripts for all stages
        event.scriptPhase1=createEventForm.script1;
        event.scriptPhase2=createEventForm.script2;
        event.scriptPhase3=createEventForm.script3;
        event.scriptPhase4=createEventForm.script4;

        //Split the event descriptions
        event.hashes=createEventForm.hashes;
        System.out.println("hashtag :" + event.hashes);
        String[] hashtags=event.getHashTags();
        for(String s: hashtags)
        {
            System.out.println("hashtag element is:"+ s);
        }

        //Add participants
        event.participants=new ArrayList<User>();

        if(createEventForm.participants != null) {
            createEventForm.participants.removeAll(Collections.singleton(null));
            for (String t : createEventForm.participants) {
                System.out.println("participants are: "+t);
                User p = User.findByFullname(t);
                System.out.println("participants fullname: "+p.fullname);
                event.participants.add(p);
            }
        }

        //create question objects
        event.Questions=new ArrayList<Question>();
        Question q=new Question();
        q.questionString=createEventForm.question;
        q.Option1=createEventForm.option1;
        q.Option2=createEventForm.option2;
        q.Option3=createEventForm.option3;
        q.Option4=createEventForm.option4;
        q.Answer=createEventForm.answer;
        System.out.println("Answer for question 1"+q.Answer);
        q.save();
        event.Questions.add(q);

        Question fq=new Question();
        fq.questionString=createEventForm.fquestion;
        fq.Option1=createEventForm.foption1;
        fq.Option2=createEventForm.foption2;
        fq.Option3=createEventForm.foption3;
        fq.Option4=createEventForm.foption4;
        fq.Answer=createEventForm.fanswer;
        System.out.println("Answer for question 1"+fq.Answer);
        fq.save();
        event.Questions.add(fq);

        //add durations for all the phases
        event.phase1Duration=Integer.valueOf(createEventForm.phaseDuration);
        event.phase2Duration=Integer.valueOf(createEventForm.phaseDuration);
        event.phase3Duration=Integer.valueOf(createEventForm.phaseDuration);

        //event active status
        event.active=0;
        //save the event
        event.save();

    }


    public static Result deleteEvent(){
        User instructor = User.findByEmail(request().username());
        Form<deleteEventForm> dform = form(deleteEventForm.class);
        deleteEventForm d = dform.bindFromRequest().get();
        //Get the corresponding event
        Event e=Event.findByName(d.eventNameToBeDeleted);
        //Mark the event as deleted- status 3
        e.active=3;
        e.update();
        return ok(deleteEventConfirmation.render((User.findByEmail(request().username())), e));
    }

    public static Result updateEvent(){
        return ok(updateEventConfirmation.render((User.findByEmail(request().username())), Event.findEvent()));

    }

    public static Result getEventstoActivate()
    {
        List<Event> eventList = Event.getAllEventsToActivate();
        StringBuilder output = new StringBuilder();
        output.append("[");
        for(int i=0;i<eventList.size();i++){
            output.append("{");
            output.append("\"eventId\":");
            output.append("\"");
            output.append(eventList.get(i).eventId);
            output.append("\"");
            output.append(",");
            output.append("\"eventName\":");
            output.append("\"");
            output.append(eventList.get(i).eventName);
            output.append("\"");
            output.append(",");
            output.append("\"Date\":");
            output.append("\"");
            output.append(eventList.get(i).eventDateTime);
            output.append("\"");
            output.append("},");
        }
        String response = output.toString();
        response = response.substring(0, response.length()-1);
        response = response + "]";
        System.out.println(response);
//        String s = "[{\"eventId\":\"jhhj\",\"eventName\":\"hjkghsjk\",\"Date\":\"jkdghs jkhsg\"}]";
        return ok(response);
    }

    public static Result getEventSummary() {
        User reportUser = User.findByEmail(request().username());
        // Event reportEvent = Event.findByName(name);
        Event reportEvent = Event.findEvent();
        return ok(report.render(reportUser, reportEvent,Event.findAllEvents()));

    }

    public static Result activateEvent(Long eventId){
        //Update the status of the event to 1 to mark that the event is activated.
        //find event by eventID
        Event event=Event.findById(eventId);
        //Call function to mark the event as ongoing
        event.markEventStatusAsOngoing();
        //update the action to the database.
        event.update();
        return ok(eventId+" Activated");
    }

    public static Result generateIndividualEventSummary(String eventName){
        //Report Analytics
        ReportAnalytics ra=new ReportAnalytics();
        //Event e=Event.findByName(eventName);
        System.out.println("going to call report analytics function...");
        ra.analyze(eventName);
        String maxPercentPhase= ra.getMaxPercentPhase();
        String hashTagMsgs=ra.getHashMsgs();
        String collabMsg=ra.getCollabMsg();
        System.out.println("Report Analytics Max Percent Phase:" + maxPercentPhase);
        System.out.println("Report Analytics Hash Messages:" + hashTagMsgs);
        System.out.println("Report Analytics collabMsg:" + collabMsg);
        String output="";
        if(maxPercentPhase!=null && hashTagMsgs!=null && collabMsg!=null) {
            output = ra.getMaxPercentPhase() + "|" + ra.getHashMsgs() + "|" + ra.getCollabMsg();
            //System.out.println("Event name "+e.eventName);
            //Finding the user with highest upvotes
            List<UserEventStats> usrEventStatsList = UserEventStats.findAllUserEventStats();
            System.out.println("The user event statistics...");
            int maxUpvotes = Integer.MIN_VALUE;
            User mostUpvotedUser = new User();
            for (int i = 0; i < usrEventStatsList.size(); i++) {
                int currentUserUpvoteCount = usrEventStatsList.get(i).noOfUpVotesReceivedForEvent;
                System.out.println("current user upvotes... " + currentUserUpvoteCount);
                System.out.println("Fetching the current user..");
                User currentUser = usrEventStatsList.get(i).user;
                System.out.println("current user name.." + currentUser.fullname);
                if (currentUserUpvoteCount > maxUpvotes) {
                    maxUpvotes = currentUserUpvoteCount;
                    mostUpvotedUser = currentUser;
                }
            }
            if (maxUpvotes > 0)
                output += "|" + " The most upvoted student for this event is " + mostUpvotedUser.fullname + " with " + maxUpvotes + " votes.";
            else
                output += "|" + " None of the students in this event were upvoted by his/her peers. ";
        }
        else
        {
            output="This event is not completed. You can view the reports for this event once it is complete";
        }
        return ok(output);
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

    public static Result instructorPastEventDiscussions() {
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
        public String hashes;

        @Constraints.Required
        public String script1;

        @Constraints.Required
        public String script2;

        @Constraints.Required
        public String script3;

        @Constraints.Required
        public String script4;

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

        //@Constraints.Required
        //public String endTime;

        @Constraints.Required
        public String phaseDuration;

        @Constraints.Required
        public String startTime;

        @Constraints.Required
        public List<String> participants;
    }

    public static class deleteEventForm{
        @Constraints.Required
        public String eventNameToBeDeleted;

    }
    public static class reportForEvent {
        @Constraints.Required
        public String event;
    }
}


