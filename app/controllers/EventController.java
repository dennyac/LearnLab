package controllers;

import models.Event;
import models.Question;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.eventSequence.eventStage1;
import views.html.eventSequence.eventStage3;
import views.html.eventSequence.eventStage4;
import views.html.eventSequence.eventResult;
import views.html.chatRoom;
import views.html.live.liveFeeds;
import views.html.live.liveStats;
import views.html.live.offlineStats;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

@Security.Authenticated(Secured.class)
public class EventController extends Controller {

    public static Result eventStage1(long eventId) {
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question =  eventSelected.Questions.get(0);
        return ok(eventStage1.render((currentUser), eventSelected, question));
    }

    public static Result chatRoom(long eventId) {
//        if(username == null || username.trim().equals("")) {
//            flash("error", "Please choose a valid username.");
//            return redirect(routes.Application.index());
//        }
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        System.out.println("Request().username:"+ currUser.fullname);
        System.out.println("User email id is " + currUser.email);
        //System.out.println("EVENT ID" + eventId);
        return ok(chatRoom.render((currUser),eventSelected));
    }

    public static Result eventStage3(long eventId) {
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question = eventSelected.Questions.get(0);
        return ok(eventStage3.render((currUser), eventSelected, question));
    }

    public static Result eventFeeds(){
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(liveFeeds.render((User.findByEmail(request().username())), eventnames));
    }

    public static Result eventStats(){
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(liveStats.render((User.findByEmail(request().username())), eventnames));
    }

    public static Result offlineEventStats(){
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(offlineStats.render((User.findByEmail(request().username())), eventnames));
    }


    public static Result eventFeedsJs(){
        return ok(views.js.live.liveFeeds.render());
    }

    public static Result eventStage4(long eventId){
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question = eventSelected.Questions.get(1);
        return ok(eventStage4.render((currUser), eventSelected, question));

    }

    public static Result eventResult(){
        return ok(eventResult.render((User.findByEmail(request().username())), Event.findEvent()));
    }
}
