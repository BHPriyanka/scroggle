/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/
package edu.neu.madcourse.priyankabh.scroggle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.neu.madcourse.priyankabh.GlobalClass;
import edu.neu.madcourse.priyankabh.MainActivity;
import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.dictionary.Acknowledgement;
import edu.neu.madcourse.priyankabh.tictactoe.GameActivity;

public class ScroggleMainFragment extends Fragment {

    private AlertDialog mDialog;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView =
                inflater.inflate(R.layout.main_fragment_scroggle, container, false);

        // Handle buttons here...
        View newButton = rootView.findViewById(R.id.new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScroggleGameActivity.class);
                getActivity().startActivity(intent);
            }
        });

        View ackButton = rootView.findViewById(R.id.ack_button);
        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up dialog
                final Dialog mDialog = new Dialog(getActivity());
               // mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setTitle("Acknowledgement");
                mDialog.setContentView(R.layout.scroggle_ack);
                mDialog.setCancelable(true);

                //set up text
                TextView text = (TextView) mDialog.findViewById(R.id.ack_scroggle);
                text.setText(R.string.scroggle_ack);

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
                text.setText(R.string.instructions);

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

        return rootView;
    }



}

