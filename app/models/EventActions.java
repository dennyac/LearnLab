package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;
import static akka.dispatch.Futures.future;
import akka.actor.Status;
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

    public Date TimeOfEventAction;

    public static Model.Finder<Long, EventActions> find = new Model.Finder<Long, EventActions>(Long.class, EventActions.class);

   //number of informal messages
    public static int noOfInformalMessages(String EventName )
    {
        return find.where().eq("event.eventName", EventName).and(Expr.like("Attribute2", null),Expr.like("ActionType", "Message")).findRowCount();

    }

    //no of hash tag messages
    public static int getHashTagMessageCount(String EventName)
    {
        return find.where().eq("event.eventName", EventName).and(Expr.like("Attribute2","#" + "%"),Expr.like("ActionType", "Message")).findRowCount();
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

//    public getInformalMessageCount( String EventName)
//    {
//        Event.findByName(EventName).g
//    }



}
