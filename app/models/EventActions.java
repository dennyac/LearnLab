package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import org.joda.time.DateTime;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Date;
import static akka.dispatch.Futures.future;
import akka.actor.Status;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import play.libs.Akka;
import scala.concurrent.Future;
import play.db.ebean.Model;


/**
 * Created by shruthir28 on 11/10/2014.
 */

@Entity
public class EventActions extends Model{

    @Id
    public Long id;

    @ManyToOne
    public Event event;

    @ManyToOne
    public User user;

    public String ActionType;  //Possible values : Join	Question(Phase1)	Message

    public String Attribute1;  //Possible values: Pre-Answer	Message Content	Post-Answer	Answer2

    public String Attribute2; //Possible values: Justification	HashTag

    public DateTime TimeOfEventAction;

    public static Model.Finder<Long, EventActions> find = new Model.Finder<Long, EventActions>(Long.class, EventActions.class);

    public static EventActions findByEventIDUserIDActionType(Event event,User user,String actionType){
        return find.where().eq("event", event).eq("user",user).eq("ActionType",actionType).findUnique();
    }

    public static List<EventActions> findHashTagMessagesByEventIDUserID(User user,Event event){
        return find.where().eq("event", event).eq("user",user).eq("Attribute2","hashTag").findList();
    }

    public static List<EventActions> getEventMessages(Event event,int minutes){
        return find.where().eq("event", event).eq("ActionType","Message").gt("TimeOfEventAction",DateTime.now().minusMinutes(minutes)).findList();
    }

    public static List<EventActions> findMessagesByEventIDUserID(User user,Event event){
        return find.where().eq("event", event).eq("user",user).eq("ActionType","Message").findList();
    }

    public static List<EventActions> findAllUpvotesForUserByEventID(User user,Event event){
        return find.where().eq("event", event).eq("Attribute1",user.id.toString()).eq("ActionType","UpVote").findList();
    }
   //number of informal messages
    public static int noOfInformalMessages(String EventName )
    {
        return find.where().eq("event.eventName", EventName).and(Expr.like("Attribute2", null),Expr.like("ActionType", "Message")).findRowCount();

    }
    public static int findNoOfInformalMessagesByEvent(Event event)
    {
        return find.where().eq("event", event).and(Expr.like("Attribute2", null),Expr.like("ActionType", "Message")).findRowCount();

    }

    //no of hash tag messages
    public static int getHashTagMessageCount(String EventName)
    {
        return find.where().eq("event.eventName", EventName).and(Expr.like("Attribute2", "hashTag"), Expr.like("ActionType", "Message")).findRowCount();
    }
    public static int findNoOfHashTagMessageCountByEvent(Event event)
    {
        return find.where().eq("event.eventName", event).and(Expr.like("Attribute2", "hashTag"), Expr.like("ActionType", "Message")).findRowCount();
    }

    //list of all the informal messages
    public static List<EventActions> findAllInformalMessagesOfEvents(Event event){
        return find.where().eq("event",event).eq("action_Type","Message").findList();
    }

    //list of all the hashTagged messages
    public static List<EventActions> findHashTagMessagesOfEvents(Event event){
        return find.where().eq("event",event).eq("Attribute2","hashTag").findList();
    }

    public static boolean hasAUserParticipatedInAnEvent(Event event, User user){
        boolean userParticipated = false;
        EventActions ea = EventActions.findByEventIDUserIDActionType(event,user,"Stage1");
        if(ea != null){
            userParticipated = true;
        }
        return userParticipated;
    }

    public static Future<String> asyncSave(final EventActions ea) { /* should the params be ServerMessageModel? */
        return future(new Callable<String>() {
            public String call() {
//                System.out.println("Entered Async call");
                ea.save();
//                System.out.println("Completed save");
                return "Success";
            }
        }, Akka.system().dispatcher());
    }

    public static List<String> getHashtagEvents()
    {
        List<String> messages = new ArrayList<String>();
        List<EventActions> hashtagEvents = find.where().eq("action_type","Message").eq("attribute2","hashTag").findList();
        for(int i=0;i<hashtagEvents.size();i++)
        {
            messages.add(hashtagEvents.get(i).Attribute1);
        }
        return messages;
    }


    public static List<String> getUserwiseHashtagEvents(){
        List<String> userwisemessages = new ArrayList<String>();
        List<EventActions> usermessages = find.where().eq("action_type","Message").eq("attribute2","hashTag").findList();
        for(int j=0;j<usermessages.size();j++){
            String temp = usermessages.get(j).user.fullname+"-"+usermessages.get(j).Attribute1;
            userwisemessages.add(temp);
        }
        return userwisemessages;
    }

//    public getInformalMessageCount( String EventName)
//    {
//        Event.findByName(EventName).g
//    }



}
