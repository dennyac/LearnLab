package controllers;

//import jdk.nashorn.internal.ir.ObjectNode;
import models.Event;
import models.Question;
import models.User;
import models.UserStats;
import models.EventStats;
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
import views.html.exceptionLandingPage;
import views.html.statistics.eventStatistics;
import views.html.statistics.individualStudentStatistics;
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
public class Statistics extends Controller {

    public static Result getEventStatistics(){
        try{
        User reportUser = User.findByEmail(request().username());
        return ok(eventStatistics.render(reportUser, Event.findAllEvents()));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at  "));
        }
    }

    public static Result generateIndividualEventStatistics(String eventName){
      try{
        //code to return the statistics of an event
        //get the statistics of the event
        Event e=Event.findByName(eventName);
        String output="";
        if(e.active==2) {
            EventStats stat = EventStats.findByEventName(eventName);
            output += "Number of participants ," + stat.noOfPraticipants + "|";
            output += "Number of formal messages(hashtags) ," + stat.noOfHashTagMessgaes + "|";
            output += "Number of informal messages(chat) ," + stat.noOfInformalMessages + "|";
            output += "Percentage of students who gave correct answer in pre test(Phase1) ," + stat.percentageCorrectInPhase1 + "% |";
            output += "Percentage of students who gave correct answer in post test(Phase3) ," + stat.percentageCorrectInPhase3 + "% |";
            output += "Percentage of students who gave correct answer to follow up question ," + stat.percentageCorrectInPhase4 + "%";
            System.out.println("The output "+output);
            return ok(output);

            //This is dummy data
            //System.out.println("Returning sample output");
            //return ok("first, row"+"|"+"second, row |"+"third, row" + "|"+"fourth, row"+"|"+"fifth, row"+"|"+"sixth, row");
    }
    else
        {
            output+="The event is not completed. You can view the statistics for this event once it is complete."+"|";
            return ok(output);
        }
      } catch (Exception e) {
          e.printStackTrace();
          return ok(exceptionLandingPage.render("Something went wrong at generate individual event statistics"));
      }
    }

    public static Result getStudentStatistics(){
        try{
        User reportUser = User.findByEmail(request().username());
        return ok(individualStudentStatistics.render(reportUser, User.findAllUsers()));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at  get student statistics"));
        }
    }

    public static Result generateIndividualStudentStatistics(String studentName){
        try{
        //code to return the statistics of a student
        User student=User.findByFullname(studentName);
        UserStats stat = UserStats.findUserStatsByUser(student);
        String output="";
        if(stat==null)
        {
            System.out.println("The student has not participated in any event till now ");
            output+="The student has not participated in any event till now ";
        }
        else
        {
            //output+="The user`s statistics are as below |";
            output+="Number of events participated till date ,"+stat.noOfEventsParticipatedIn +"|";
            output+="Total Number of informal messages ,"+stat.noOfIndividualInformalMessages +"|";
            output+="Total Number of formal messages ,"+stat.noOfIndividualHashTagMessages +"|";
            output+="Total Number of messages ,"+stat.noOfMessagesExchangedByAllInAllEventsUserParticipated +"|";
            output+="Contribution of the user to discussions (percentage) ,"+stat.percentageContributionForDiscussion +"|";
            output+="Cognitive Ability Score ,"+ stat.cognitiveAbilitiesScore+"|";
            output+="Number of upvotes received till date ,"+ stat.upVotes+"|";
            output+="Aggregate Score ,"+stat.aggregateScore;
        }
        System.out.println("student name is "+ studentName);
        return ok(output);
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at generate individual student statistics"));
        }
    }

    public static Result getEventMessageStatsPie(String eventName){

        EventStats e = EventStats.findByEventName(eventName);
        double im = e.noOfInformalMessages;
        double fm = e.noOfHashTagMessgaes;
        StringBuilder output = new StringBuilder();
        output.append("[");
        output.append("{");
        output.append("\"age\":");
        output.append("\"");
        output.append("Informal Messages");
        output.append("\"");
        output.append(",");
        output.append("\"population\":");
        output.append("\"");
        output.append(String.valueOf(im));
//            output.append("30");
        output.append("\"");
        output.append("},");
        output.append("{");
        output.append("\"age\":");
        output.append("\"");
        output.append("Hashtag Messages");
        output.append("\"");
        output.append(",");
        output.append("\"population\":");
        output.append("\"");
        output.append(String.valueOf(fm));
//            output.append("50");
        output.append("\"");
        output.append("}");
        String response = output.toString();
        response = response + "]";
        return ok(response);
    }

    public static Result getEventMessageStatsBG(String eventName){

        EventStats e = EventStats.findByEventName(eventName);
        double p1 = e.percentageCorrectInPhase1/2;
        double p2 = e.percentageCorrectInPhase3/2;
        double p3 = e.percentageCorrectInPhase3/2;
        Double[] p = new Double[3];
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;

        StringBuilder output = new StringBuilder();
        output.append("[");
        for(int i=0;i<3;i++)
        {
            output.append("{");
            output.append("\"State\":");
            output.append("\"");
            output.append("Phase"+String.valueOf(i));
            output.append("\"");
            output.append(",");
            output.append("\"Correct Answers\":");
            output.append("\"");
            output.append(p[i]);
            output.append("\"");
            output.append(",");
            output.append("\"Incorrect Answers\":");
            output.append("\"");
            output.append(50-p[i]);
            output.append("\"");
            output.append("},");
        }
        String response = output.toString();
        response = response.substring(0, response.length()-1);
        response = response + "]";

        return ok(response);
    }
}