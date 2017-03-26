package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 3/12/2017.
 */

public class TwoPlayerWordGameFragment extends Fragment {

    private ProgressDialog progressDialog;
    private boolean isPlayer2;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle  b = getActivity().getIntent().getExtras();

        if (b != null) {
            isPlayer2 = b.getBoolean("isPlayer2");
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
                intent.putExtra("isPlayer2", isPlayer2);
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

