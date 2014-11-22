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

    @OneToOne(mappedBy = "eventStats")
    public Event event;

    public int noOfPraticipants;

    public int totalNoOfMessages;

    public int noOfHashTagMessgaes;

    public int noOfInformalMessages;

    public float percentageCorrectInPhase1;

    public float percentageCorrectInPhase3;

    public float percentageCorrectInPhase4;

    public float positiveCollaborationScore;

    public float negativeCollaborationScore;

}
