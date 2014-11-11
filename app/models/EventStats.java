package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by shruthir28 on 11/10/2014.
 */
@Entity
public class EventStats {

    @Id
    public Long id;

    @OneToOne(mappedBy = "eventStats")
    public Event event;

    public int TotalMessages;

    public double percentOfSuccess; //% of who got it right

    //collaboration index

   //ration between formal and informal messgae

    //correction rate



}
