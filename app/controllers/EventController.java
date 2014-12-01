package controllers;

import controllers.util.EventStatsWrapper;
import controllers.util.EventUtils;
import controllers.util.EventUtils.EventStageform;
import controllers.util.MD5Util;
import controllers.util.UserEventStatsWrapper;
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
import views.html.exceptionLandingPage;
import views.html.live.liveFeeds;
import views.html.live.liveStats;
import views.html.live.offlineStats;
import static play.libs.Json.toJson;

import java.util.*;

import java.util.ArrayList;

import static play.data.Form.form;
import scala.collection.JavaConverters;


@Security.Authenticated(Secured.class)
public class EventController extends Controller {

    public static Result eventStage1(long eventId) {
        try{
        User currentUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        String[] hashTagsFromEvent = eventSelected.getHashTags();
        List<String> hashTags = Arrays.asList(hashTagsFromEvent);
        Question question =  eventSelected.Questions.get(0);
        return ok(eventStage1.render((currentUser), eventSelected, hashTags , question,form(EventStageform.class)));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at  event stage 1"));
        }
    }

    public static Result chatRoom(long eventId) {
        try{
        Form<EventStageform> form1 = form(EventStageform.class);
        EventStageform f = form1.bindFromRequest().get();
        f.eventAction = "Stage1";
        EventUtils.initEventStage(f);
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        Question question = eventSelected.Questions.get(0);
        String[] hashTagsFromEvent = eventSelected.getHashTags();
        List<String> hashTags = Arrays.asList(hashTagsFromEvent);
        return ok(chatRoom.render((currUser),eventSelected,hashTags, question));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at chat room "));
        }
    }

    public static Result eventStage3(long eventId) {
        try{
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        String[] hashTagsFromEvent = eventSelected.getHashTags();
        List<String> hashTags = Arrays.asList(hashTagsFromEvent);
        Question question = eventSelected.Questions.get(0);
        return ok(eventStage3.render((currUser), eventSelected,hashTags, question,form(EventStageform.class)));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at  eventStage 3"));
        }
     }

    public static Result eventStage4(long eventId){
        try{
        Form<EventStageform> form3 = form(EventStageform.class);
        EventStageform f = form3.bindFromRequest().get();
        f.eventAction = "Stage3";
        EventUtils.initEventStage(f);
        User currUser = User.findByEmail(request().username());
        Event eventSelected = Event.findById(eventId);
        String[] hashTagsFromEvent = eventSelected.getHashTags();
        List<String> hashTags = Arrays.asList(hashTagsFromEvent);
        Question question = eventSelected.Questions.get(1);
        List<User> userParticipants = eventSelected.participants;
        return ok(eventStage4.render((currUser), eventSelected, hashTags, question,userParticipants ,form(EventStageform.class)));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at eventStage4 "));
        }
     }

    public static Result eventResult(){
        try{
        Form<EventStageform> form4 = form(EventStageform.class);
        EventStageform f = form4.bindFromRequest().get();
        f.eventAction = "Stage4";
        if(f.upVotedPeers != null) {
            f.upVotedPeers.removeAll(Collections.singleton(null));
            for (String t : f.upVotedPeers) {
                System.out.println("Peer Chosen for upvote:" + t);
            }
        }
        EventUtils.initEventStage(f);

        //Retrieving event to set the event as complete.
        long eventId = Long.parseLong(f.eventId);
        Event eventSelected = Event.findById(eventId);
            eventSelected.markEventStatusAsCompleted();
        eventSelected.update();

        //[TODO]Commented this out as it will be handled in the CRON JOB  [To be handled by Denny]
        //EventStatsWrapper eventStatsWrapper = EventUtils.EventAggregator(Event.findById(Long.parseLong(f.eventId)), User.findById(f.userId));
        //;
        //return ok(eventResult.render((User.findByEmail(request().username())), Event.findEvent(),eventStatsWrapper));
        return ok(eventResult.render((User.findByEmail(request().username())), Event.findEvent()));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at event results "));
        }
    }

    public static Result eventFeeds(){
    try{
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(liveFeeds.render((User.findByEmail(request().username())), eventnames));
    } catch (Exception e) {
        e.printStackTrace();
        return ok(exceptionLandingPage.render("Something went wrong at event feeds"));
    }
    }

    public static Result eventStats(){
        try{
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(liveStats.render((User.findByEmail(request().username())), eventnames));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at event stats "));
        }
     }

    public static Result offlineEventStats(){
        try{
//        Event.populateEvents();
        ArrayList<String> eventnames = new ArrayList<String>();
        eventnames.add("EventGroup1");
        eventnames.add("EventGroup2");
        return ok(offlineStats.render((User.findByEmail(request().username())), eventnames));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at offline event stats "));
        }
    }

    public static Result getChatPhaseEvents(){
        try{return ok(toJson(Event.getChatPhaseEvents()));
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at get chat phase events "));
        }
     }

    public static Result eventFeedsJs(){
        try{
        return ok(views.js.live.liveFeeds.render());
        } catch (Exception e) {
            e.printStackTrace();
            return ok(exceptionLandingPage.render("Something went wrong at  event feeds Js"));
        }
    }

}
