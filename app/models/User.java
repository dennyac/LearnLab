package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: yesnault
 * Date: 20/01/12
 */
@Entity
@Table(name = "users")
public class User extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;


    @OneToMany(fetch=FetchType.LAZY, mappedBy = "instructor") @JsonBackReference
    public List<Event> events;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String fullname = "";

    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;

    @Formats.NonEmpty
    public Boolean isInstructor = false;

    @Constraints.Required
    @Formats.NonEmpty
    public String bio;

    @ManyToMany(fetch=FetchType.LAZY, mappedBy = "participants") @JsonBackReference
    public List<Event> EventsParticipated;

    @OneToOne(fetch=FetchType.LAZY, mappedBy = "user")  @JsonBackReference
    public UserStats userStatsInformation;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user")  @JsonBackReference
    public List<UserEventStats> listOfUserEventStats;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user") @JsonBackReference
    public List<EventActions> actions;

    // -- Queries (long id, user.class)
    public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */

    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static User findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }


    /**
     * Retrieve a user from a fullname.
     *
     * @param fullname Full name
     * @return a user
     */
    public static User findByFullname(String fullname) {
        return find.where().eq("fullname", fullname).findUnique();
    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    public static User findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    //Returns all the users in the table
    public static List<User> findAllUsers()
    {
        return find.findList();
    }

//    public static void testList()
//    {
//        List<User> u = findAllUsers();
//        for( User ur : u)
//        {
//            System.out.println(" the name is " + ur.fullname);
//        }
//    }

    /**
     * Authenticate a User, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static User authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        User user = find.where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }




    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise.
     * @throws AppException App Exception
     */
    public static boolean confirm(User user) throws AppException {
        if (user == null) {
            return false;
        }

        user.confirmationToken = null;
        user.validated = true;
        user.save();
        return true;
    }

    
}
