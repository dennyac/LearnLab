package controllers;

import controllers.util.LeaderListElementWrapper;
import controllers.util.MD5Util;
import models.Event;
import models.EventActions;
import models.User;
import models.UserEventStats;
import models.UserStats;
import models.utils.AppException;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.dashboard.help;
import views.html.user.userProfile;
import views.html.user.pastEventDiscussion;
import views.html.user.leaderBoard;
import views.html.user.pastEventsForUsersView;

import javax.validation.Constraint;
import scala.collection.JavaConverters;
import java.util.List;

import java.util.List;

import static play.data.Form.form;

/**
 * Login and Logout.
 * User: yesnault
 */
public class Application extends Controller {

    public static Result GO_HOME = redirect(
            routes.Application.index()
    );

    public static Result GO_DASHBOARD = redirect(
            routes.Dashboard.index()
    );

    /**
     * Display the login page or dashboard if connected
     *
     * @return login page or dashboard
     */
    public static Result index() {
        // Check that the email matches a confirmed user before we redirect
        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email);
            if (user != null && user.validated) {
                return GO_DASHBOARD;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }

        return ok(index.render(form(Register.class), form(Login.class)));
    }
    /**
     * Login class used by Login Form.
     */
    public static class Login {

        @Constraints.Required
        public String email;
        @Constraints.Required
        public String password;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            User user = null;
            try {
                user = User.authenticate(email, password);
            } catch (AppException e) {
                return Messages.get("error.technical");
            }
            if (user == null) {
                return Messages.get("invalid.user.or.password");
            } else if (!user.validated) {
                return Messages.get("account.not.validated.check.mail");
            }
            return null;
        }

    }

    public static class Register {

        @Constraints.Required
        public String email;

        @Constraints.Required
        public String fullname;

        @Constraints.Required
        public String inputPassword;

        @Constraints.Required
        public boolean isInstructor;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(email)) {
                return "Email is required";
            }

            if (isBlank(fullname)) {
                return "Full name is required";
            }

            if (isBlank(inputPassword)) {
                return "Password is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }

    /**
     * Handle login form submission.
     *
     * @return Dashboard if auth OK or login form if auth KO
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();

            Form<Register> registerForm = form(Register.class);

            if (loginForm.hasErrors()) {
                return badRequest(index.render(registerForm, loginForm));
            } else {
                session("email", loginForm.get().email);
            return GO_DASHBOARD;
        }
    }

    /**
     * Logout and clean the session.
     *
     * @return Index page
     */
    public static Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return GO_HOME;
    }

    public static Result profile(){

        return ok(userProfile.render((User.findByEmail(session().get("email"))), Event.findEvent(), MD5Util.md5Hex(session().get("email"))));
    }

    public static Result pastEventDiscussion(Long userid){
        List<Event> userCompletedEvents = UserEventStats.findUserCompletedEvents(userid);
        List<Event> otherCompletedEvents = UserEventStats.findOtherCompletedEvents(userid);
        User u = User.findById(userid);
        UserStats userStats = null;
        if(!u.isInstructor){
            userStats = UserStats.findUserStatsByUser(u);
        }
        return ok(pastEventDiscussion.render(u, userCompletedEvents, otherCompletedEvents, userStats));
    }

    public static Result pastEventDiscussionView(Long eventId, Long userId){
        List<String> userwiseMessages = EventActions.getUserwiseHashtagEvents();
        List<String> eventMessages = EventActions.getHashtagEvents();
//        User u = User.findByEmail(request().username());
        User u = User.findById(userId);
        return ok(pastEventsForUsersView.render(u,eventMessages,userwiseMessages));
    }

    public static Result leaderBoard(Long userId){
        //retrieve userStats and the side pane
        User currentUser = User.findById(userId);
        UserStats userStats = UserStats.findUserStatsByUser(currentUser);

        //retrieve the leader list from the wrapper
        LeaderListElementWrapper le = new LeaderListElementWrapper();
        List<LeaderListElementWrapper> leaderList = le.prepareLeaderList();
        return ok(leaderBoard.render(currentUser,userStats, Event.findEvent(),leaderList));
    }

}