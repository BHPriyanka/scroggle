package edu.neu.madcourse.priyankabh.communication.fcm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.communication.realtimedatabase.models.User;

/**
 * Created by priya on 3/3/2017.
 */

public class RegisterUserActivity extends Activity {
    private DatabaseReference mDatabase;
    private CoordinatorLayout coordinatorLayout;
    private String chosenUserKey="";

    // Please add the server key from your firebase console in the follwoing format "key=<serverKey>"
    private static final String SERVER_KEY = "key=AAAA3ITLrYc:APA91bEO4XNNsyoIbhH4T9y_NqaKMstR2BwSAgCG9I8-m9JzsKrzxi9XhNOArq2ShRPSM6mrOwvYj2-11o4JDVML2Oqca7HwAe13xcIssT7Z2dJX9Wg9G1ydLOOijzn47tUt7PG_Joq5";

    private String userName="";
    private static final String TAG = FCMActivity.class.getSimpleName();
    private String userScore="";
    private String userToken="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_registration);
        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            userName = b.getString("userName");
            userScore = b.getString("score");
            userToken = b.getString("token");
        }

        if(isNetworkAvailable(getApplicationContext())){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            if(globalVariable.usersMap.containsKey(userToken)){
                String str = "User already registered";
                Toast.makeText(RegisterUserActivity.this, str, Toast.LENGTH_SHORT).show();
            } else {
                writeNewUser(userToken, userScore, userName);
                String msg = "Registered Successfully";
                Toast.makeText(RegisterUserActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        } else{
             Log.d(TAG,"Internet not available");

            final Dialog mDialog = new Dialog(RegisterUserActivity.this);
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
            mDialog.show();
            /*Snackbar snackbar = Snackbar
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
            snackbar.show();*/
        }
    }


    private void writeNewUser(String userId, String score, String name) {
        User user = new User(name, score, userId);
        mDatabase.child("users").child(userId).setValue(user);
    }


    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }

}
