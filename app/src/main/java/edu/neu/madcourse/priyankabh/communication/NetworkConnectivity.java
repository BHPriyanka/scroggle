package edu.neu.madcourse.priyankabh.communication;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.communication.CommunicationActivity;

/**
 * Created by priya on 3/9/2017.
 */

public class NetworkConnectivity extends BroadcastReceiver {
    public NetworkConnectivity(){

    }

    private static String TAG = "NetworkConnection";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Check if activity is visible or not
        try{
            boolean isVisible = GlobalClass.isActivityVisible();
            Log.d(TAG, "Activity is Visible");
            //If its visible, trigger task, else do nothing
            if(isNetworkAvailable(context)){
                new CommunicationActivity().changeTextStatus(true);
            } else {
                new CommunicationActivity().changeTextStatus(false);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }

}
