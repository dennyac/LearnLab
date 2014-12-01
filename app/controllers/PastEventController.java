package controllers;

import controllers.util.EventAverageWrapper;
import models.*;
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
import views.html.pastEvents.pastEventsForInstructorsView;
import views.html.pastEvents.pastUserEventPerformaceView;

import java.util.*;

import scala.collection.JavaConverters;
import java.net.MalformedURLException;
import models.utils.AppException;
import play.Logger;

import static play.data.Form.form;
import java.text.*;
/**
 * Created by ANIL on 11/26/2014.
 */
@Security.Authenticated(Secured.class)
public class PastEventController extends Controller {


    public static Result pastEventsViewer(Long eventId)
    {
        List<String> userwiseMessages = EventActions.getUserwiseHashtagEvents();
        List<String> eventMessages = EventActions.getHashtagEvents();
        User reportUser = User.findByEmail(request().username());
        return ok(pastEventsForInstructorsView.render(reportUser,eventMessages,userwiseMessages));
    }

    public static Result pastUserEventStatistics(Long userId, Long eventId)
    {
        User currentUser =  User.findById(userId);
        Event eventSelected = Event.findById(eventId);
        System.out.println("Event ID:"+ eventId);
        EventStats eventStats = EventStats.findByEventId(eventId);
        EventAverageWrapper eventAverageWrapper = new EventAverageWrapper(eventStats);
        UserEventStats userEventStats = UserEventStats.findEventSpecificStatsForUserStats(currentUser,eventSelected);
        return ok(pastUserEventPerformaceView.render(currentUser, eventSelected, eventAverageWrapper, userEventStats));
    }

}
