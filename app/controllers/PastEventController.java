package controllers;

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
import views.html.pastEvents.pastEventsForInstructorsView;
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
 * Created by ANIL on 11/26/2014.
 */
@Security.Authenticated(Secured.class)
public class PastEventController extends Controller {


    public static Result pastEventsViewer(Long EventId){
        User reportUser = User.findByEmail(request().username());
        return ok(pastEventsForInstructorsView.render(reportUser));
    }




}
