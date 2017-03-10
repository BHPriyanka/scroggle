package edu.neu.madcourse.priyankabh;

/**
 * Created by priya on 2/2/2017.
 */

import android.app.Application;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalClass extends Application{

    public static Map<String,ArrayList<String>> list = new HashMap<String,ArrayList<String>>();
    public static List<String> nineLetterWords = new ArrayList<String>();
    public static String token="";
    public static ArrayList<String> names = new ArrayList<>();
    public static Map<String,Object> usersMap = new HashMap<String,Object>();

    // Gloabl declaration of variable to use in whole app
    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }


}