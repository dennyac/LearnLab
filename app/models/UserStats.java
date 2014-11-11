package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.ArrayList;

/**
 * Created by shruthir28 on 11/10/2014.
 */

@Entity
public class UserStats {


    @Id
    public Long id;

    @OneToOne(mappedBy = "stats")
    public User user;

    public ArrayList<Badge> BadgesAcquired;

    public int NoOfPosts;

}
