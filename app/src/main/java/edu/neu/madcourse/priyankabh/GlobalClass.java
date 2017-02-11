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

}