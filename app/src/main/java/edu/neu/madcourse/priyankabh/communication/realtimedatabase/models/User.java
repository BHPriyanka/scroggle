package edu.neu.madcourse.priyankabh.communication.realtimedatabase.models;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by priya on 2/26/2017.
 */

@IgnoreExtraProperties
public class User {
    public String username;
    public String score;
    public String token;
    public String wordFormed;


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String score, String token){
        this.username = username;
        this.score = score;
        //this.email =mail;
        this.token = token;
    }
}
