package edu.neu.madcourse.priyankabh.note2map;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.note2map.models.Friend;
import edu.neu.madcourse.priyankabh.note2map.models.User;

import static edu.neu.madcourse.priyankabh.note2map.Note2MapChooseNoteType.NOTE_TYPE;
import static edu.neu.madcourse.priyankabh.note2map.SelectEventTimeActivity.NOTE_TIME;

public class Note2MapSelectFriendsActivity extends AppCompatActivity {

    private ArrayList<String> usernames;
    private User currentUser;
    private ListView listView;
    private String noteTime;
    private String noteType;
    private Bundle b;
    private Note2MapCustomAdaptorToChooseFriends customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.n2m_select_friends_activity);

        noteType = getIntent().getStringExtra(NOTE_TYPE);
        noteTime = getIntent().getStringExtra(NOTE_TIME);

        b = getIntent().getExtras();
        if(b!=null){
            currentUser = (User) b.getSerializable("currentUser");
        }
        listView = (ListView) findViewById(R.id.n2m_listview_selectfriends);

        customAdapter = new Note2MapCustomAdaptorToChooseFriends(this, currentUser);
        listView.setAdapter(customAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClickChooseFriend(View view){
        String result = "Selected Friends are :";
        for (Friend p : customAdapter.getBox()) {
            if (p.isSelected()){
                result += "\n" + p.getName();
            }
        }

        Intent intent = new Intent(Note2MapSelectFriendsActivity.this, Note2MapSearchLocationActivity.class);
        intent.putExtra(NOTE_TYPE,noteType);
                //Append all times pipe separated to the last screen to select location before create it
        intent.putExtra(NOTE_TIME,noteTime);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
    }
}
