package models;

import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Id;

/**
 * Created by Supriya on 22/11/2014.
 */

@Entity
public class UserEventStats {

    @Id
    public Long userEventId;

    @ManyToOne
    public User user;

    @ManyToOne
    public Event event;

    @ManyToOne
    public UserStats userStats;

    @Formats.NonEmpty
    public boolean phase1AnswerInEvent;

    @Formats.NonEmpty
    public boolean phase3AnswerInEvent;

    @Formats.NonEmpty
    public boolean phase4AnswerInEvent;

    @Formats.NonEmpty
    public int noOfIndividualInformalMessagesInEvent;

    @Formats.NonEmpty
    public int getNoOfIndividualHashTagMessagesInEvent;

    @Formats.NonEmpty
    public float scorePhase1InEvent;

    @Formats.NonEmpty
    public float scorePhase3InEvent;

    @Formats.NonEmpty
    public float scorePhase4InEvent;

    @Formats.NonEmpty
    public float aggregatedScoreForEvent;

    @Formats.NonEmpty
    public float collaborativeIndexForEvent;
}
