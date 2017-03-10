package edu.neu.madcourse.priyankabh.communication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.fcm.FCMActivity;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.RealtimeDatabaseActivity;

/**
 * Created by priya on 2/26/2017.
 */

public class CommunicationActivity extends Activity {
    private BroadcastReceiver broadcastReceiver;
    private static String TAG="NetworkConnection";
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*final Dialog mDialog = new Dialog(NetworkConnReceiver.this);
                //mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setTitle("Network Error");
                mDialog.setContentView(R.layout.network_error);
                mDialog.setCancelable(true);

                //set up text
                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mDialog != null)
                            mDialog.dismiss();
                            }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();*/

                if(!isNetworkAvailable(context)) {
                    Log.d(TAG, "Internet not available");
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });

                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        };
    }


    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }

    public void openFCMActivity(View view) {
        startActivity(new Intent(CommunicationActivity.this, FCMActivity.class));
    }

    public void openDBActivity(View view) {
        startActivity(new Intent(CommunicationActivity.this, RealtimeDatabaseActivity.class));
    }
}
