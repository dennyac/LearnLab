package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * Created by shruthir28 on 11/10/2014.
 */

@Entity
public class EventActions {

    @Id
    public Long id;

    @ManyToOne
    public Event event;

    @ManyToOne
    public User user;

    public static String ActionType;  //Possible values : Join	Question(Phase1)	Message

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

//    public getInformalMessageCount( String EventName)
//    {
//        Event.findByName(EventName).g
//    }



}
