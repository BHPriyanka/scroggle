package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 3/12/2017.
 */

public class TwoPlayerWordGameFragment extends Fragment {
    private ProgressDialog progressDialog;
    private boolean isPlayer1 =true;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle  b = getActivity().getIntent().getExtras();

        if (b != null) {
            isPlayer1 = b.getBoolean("isPlayer1");
        }

        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();

        if(globalVariable.nineLetterWords.isEmpty()){
           /* progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMax(100);
            progressDialog.setMessage("Please wait....");
            progressDialog.setTitle("Scroggle");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();*/
            new TwoPlayerWordGameFragment.LoadWords().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =
                inflater.inflate(R.layout.fragment_word_game_twoplayer, container, false);

        // Handle buttons here...
        View newButton = rootView.findViewById(R.id.new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScroggleTwoPlayerGameActivity.class);
                intent.putExtra("isPlayer1", isPlayer1);
                getActivity().startActivity(intent);
            }
        });

        View ackButton = rootView.findViewById(R.id.ack_button);
        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
                mDialog.setTitle("Acknowledgement");
                mDialog.setContentView(R.layout.scroggle_ack);
                mDialog.setCancelable(true);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ack_scroggle);
                text.setText("Links referred: " +
                        "1. https://developers.google.com/games/services/android/turnbasedMultiplayer" +
                        "\n"
                        + "2. http://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android+" +
                        "\n"+
                "3. http://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html" +
                "\n"+
                "4. https://github.com/playgameservices/android-basic-samples");
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
            }
        });

        View insButton = rootView.findViewById(R.id.instruction);
        insButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
                mDialog.setTitle("Instructions");
                mDialog.setContentView(R.layout.instructions);
                mDialog.setCancelable(true);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ins_button);
                text.setText("Scroggle is a 2-player board game to identify words in 3 minutes from a 9x9 grid. Each player forms words on the board and switches turns every 15 seconds. Words formed in smaller 3x3 grids fetch triple the points than those across the 3x3 grids. The letters once used to form a word cannot be reused to form other words through the rest of the game. At the end of each turn, the board is refreshed. However, the previously used letters stay intact. \n" +
                        "\n" +
                        "During player A’s turn, if player B finds arbitrage opportunities where player A may score more, then, player B might disturb the board by shaking his/her phone. This cheat leads to player B losing points worth 15% of his/her score. Player A then restarts his/her turn as if it were a new turn and is rewarded the points that player B lost. When a player quits in the middle of the game, the opponent wins.\n" +
                        "\n" +
                        "In the synchronous mode, a player may choose an opponent to play with or may play a random online opponent. In the asynchronous mode, when a player starts a new game, he/she is added to a player queue and awaits other players to join as opponents. If the player queue is not empty, the first player from the player queue is matched as an opponent to the waiting player. Push notifications are then sent to both players to accept the game with their opponents. If either player rejects the opponent, he/she is added at the end of the player queue and is re-matched with a new player from the queue. Note: If there are only two players and if either of them reject the other, then the queue may repoll the same player again. \n" +
                        "\n" +
                        "If either of the players are offline, the game enters into an asynchronous mode, and the offline player is notified of his/her turn after the online player has finished his/her turn.\n");

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
            }
        });

        /*Button registerButton = (Button) rootView.findViewById(R.id.registerUser);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
                //mDialog.setTitle("Enter your Full Name");
                mDialog.setContentView(R.layout.register_user);
                mDialog.setCancelable(true);

                //set up text
                Button ok_button = (Button) mDialog.findViewById(R.id.ok_button);
                ok_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        String score = "";
                        EditText text = (EditText) mDialog.findViewById(R.id.user_name);
                        intent.putExtra("userName", text.getText().toString());
                        intent.putExtra("score", score);
                        String token = FirebaseInstanceId.getInstance().getToken();
                        intent.putExtra("token", token);
                        startActivity(intent);
                        if (mDialog != null)
                            mDialog.dismiss();

                    }
                });
                //now that the dialog is set up, it's time to show it
                mDialog.show();
            }
        });*/


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        progressDialog.dismiss();
    }

    private class LoadWords extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
            try {
                InputStream strF = getResources().getAssets().open("nineLetterWords");
                ObjectInputStream ois=new ObjectInputStream(strF);

                globalVariable.nineLetterWords = (ArrayList<String>)ois.readObject();

                ois.close();

              /*  int count = 0;
                try {
                    while(count<100) {
                        Thread.sleep(200);
                        count +=1;
                        publishProgress(count);
                    }

                }catch(InterruptedException ie){
                    System.err.print(ie);
                }*/

            } catch(IOException e) {
                System.err.print(e);
            } catch(ClassNotFoundException ce ){
                System.err.print(ce);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
           // progressDialog.setProgress(params[0]);
        }

        protected void onPostExecute(Void v) {
           /* if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
        }
    }

}

