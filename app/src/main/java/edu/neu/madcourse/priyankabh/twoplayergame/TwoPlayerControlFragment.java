package edu.neu.madcourse.priyankabh.twoplayergame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleFragment;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleGameActivity;

/**
 * Created by priya on 3/13/2017.
 */

public class TwoPlayerControlFragment extends Fragment {

    private TwoPlayerScroggleFragment sFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.twoplayer_scroggle_fragment_control, container, false);
        final View pause = rootView.findViewById(R.id.player_pause_button);
        final View resume = rootView.findViewById(R.id.player_button_resume);
        final View quit = rootView.findViewById(R.id.player_quit_button);

        resume.setVisibility(View.INVISIBLE);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.onScrogglePause();
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.GONE);
                ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.onScroggleResume();
            }
        });

        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.onScroggleQuit();
            }
        });

        final View mute = rootView.findViewById(R.id.player_mute);
        final View unmute = rootView.findViewById(R.id.player_unmute);
        unmute.setVisibility(View.INVISIBLE);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.mute();
                mute.setVisibility(View.GONE);
                unmute.setVisibility(View.VISIBLE);
            }
        });

        unmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleTwoPlayerGameActivity) getActivity()).sFragment.mute();
                unmute.setVisibility(View.GONE);
                mute.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

}

