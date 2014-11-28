package controllers.util;

import models.UserStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Supriya on 27/11/2014.
 */
public class LeaderListElementWrapper {
    public int rank;
    public UserStats userStats;

    public LeaderListElementWrapper(){
        this.rank = 0;
        this.userStats = null;
    }

    public LeaderListElementWrapper(int rank,UserStats userStats){
        this.rank = rank;
        this.userStats = userStats;
    }

    public List<LeaderListElementWrapper> prepareLeaderList(){
        List<LeaderListElementWrapper> leaderListElementWrapperList = new ArrayList<LeaderListElementWrapper>();
        List<UserStats> leaderList = UserStats.findLeaderList();
        int rank = 1;
        for(UserStats u: leaderList){
            LeaderListElementWrapper element = new LeaderListElementWrapper(rank,u);
            leaderListElementWrapperList.add(element);
            rank++;
        }
        return leaderListElementWrapperList;
    }
}
