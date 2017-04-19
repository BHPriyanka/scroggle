package edu.neu.madcourse.priyankabh.twoplayergame.models;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by priya on 2/26/2017.
 */

@IgnoreExtraProperties
public class Player {
    public String username;
    public String token;
    public String score;
    public String gameState;
    public String opponent;
    public int gameID;
    public long totalPlayerTime;
    public long totalGameTime;
    public String grids;

    public Player(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Player(String username, String score, String token, String gameState){
        this.username = username;
        //this.gameState = state;
        this.score = score;
        this.gameState = gameState;
        this.gameID = 0;
        this.totalPlayerTime = 90000;
        totalGameTime = 180000;

    }
}
