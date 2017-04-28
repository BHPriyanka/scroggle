package edu.neu.madcourse.priyankabh.note2map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import edu.neu.madcourse.priyankabh.note2map.models.User;

public class Note2MapMainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;

    private DatabaseReference mDatabase;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            /////Creating/checking user
            mDatabase.child("users").child(FirebaseInstanceId.getInstance().getToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUser = snapshot.getValue(User.class);
                        startService(new Intent(Note2MapMainActivity.this,MyLocationService.class));

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent alarmIntent = new Intent(Note2MapMainActivity.this, MyLocationService.class);
                        PendingIntent pending = PendingIntent.getService(Note2MapMainActivity.this, 0, alarmIntent, 0);
                        if (alarmManager!= null) {
                            alarmManager.cancel(pending);
                        }
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() +
                                        60 * 1000, 60 * 1000, pending);
                        Intent intent = new Intent(Note2MapMainActivity.this, Note2MapNotesActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Note2MapMainActivity.this, Note2MapChooseUsername.class);
                        startActivity(intent);
                        Note2MapMainActivity.this.finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    /////Creating/checking user
                    mDatabase.child("users").child(FirebaseInstanceId.getInstance().getToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("Main Activity","User Exist");
                                currentUser = snapshot.getValue(User.class);
                                startService(new Intent(Note2MapMainActivity.this,MyLocationService.class));

                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                Intent alarmIntent = new Intent(Note2MapMainActivity.this, MyLocationService.class);
                                PendingIntent pending = PendingIntent.getService(Note2MapMainActivity.this, 0, alarmIntent, 0);
                                if (alarmManager!= null) {
                                    alarmManager.cancel(pending);
                                }
                                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        SystemClock.elapsedRealtime() +
                                                60 * 1000, 60 * 1000, pending);
                                Intent intent = new Intent(Note2MapMainActivity.this, Note2MapNotesActivity.class);
                                intent.putExtra("currentUser", currentUser);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Note2MapMainActivity.this, Note2MapChooseUsername.class);
                                startActivity(intent);
                                Note2MapMainActivity.this.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });

                } else {
                    finish();
                    System.exit(0);
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////Slide Menu///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    /*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.n2m_note_action_menu_new:
                Intent intent = new Intent(Note2MapMainActivity.this, Note2MapChooseNoteType.class);
                intent.putExtra("currentUser", currentUser);
                this.startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.n2m_note_action_menu, menu);
        return true;
    }
    */
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

}
