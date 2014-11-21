package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Constraint;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Created by shruthir28 on 11/10/2014.
 */
@Entity
public class EventStats extends Model {

    @Id
    public Long id;

//    @OneToOne(mappedBy = "eventStats")
//    public Event event;

   @Constraints.Required
   public String answer;
//
//    @Constraints.Required
//    public Long eventId;
//
//    @Constraints.Required
//    public int noOfHashTaggedMessages;
//
//    @Constraints.Required
//    public int noOfInformal;
//
//    @Constraints.Required
//    public int noOf




    // /public int TotalMessages;



    //public double percentOfSuccess; //% of who got it right

    //collaboration index

   //ration between formal and informal messgae

    //correction rate



}
