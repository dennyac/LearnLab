package controllers;

import controllers.util.EventUtils;
import controllers.util.EventUtils.EventStageform;
import models.*;
import play.data.Form;
import play.data.validation.Constraints;
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

import static play.data.Form.form;


@Security.Authenticated(Secured.class)
public class EventController extends Controller {

    public static Result eventStage1(long eventId) {
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question =  eventSelected.Questions.get(0);
        return ok(eventStage1.render((currentUser), eventSelected, question,form(EventStageform.class)));
    }

    public static Result chatRoom(long eventId) {
        Form<EventStageform> form1 = form(EventStageform.class);
        EventStageform f = form1.bindFromRequest().get();
        f.eventAction = "Stage1";
        EventUtils.initEventStage(f);
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        return ok(chatRoom.render((currUser),eventSelected));
    }

    public static Result eventStage3(long eventId) {
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question = eventSelected.Questions.get(0);
        return ok(eventStage3.render((currUser), eventSelected, question, form(EventStageform.class)));
    }

    public static Result eventStage4(long eventId){
        Form<EventStageform> form3 = form(EventStageform.class);
        EventStageform f = form3.bindFromRequest().get();
        f.eventAction = "Stage3";
        EventUtils.initEventStage(f);

        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question = eventSelected.Questions.get(1);
        return ok(eventStage4.render((currUser), eventSelected, question, form(EventStageform.class)));
    }

    public static Result eventResult(){
        Form<EventStageform> form4 = form(EventStageform.class);
        EventStageform f = form4.bindFromRequest().get();
        f.eventAction = "Stage4";
        EventUtils.initEventStage(f);
        EventUtils.EventAggregator(Event.findById(Long.parseLong(f.eventId)),User.findByFullname(f.fullName));
        return ok(eventResult.render((User.findByEmail(request().username())), Event.findEvent()));
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

}
