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

}