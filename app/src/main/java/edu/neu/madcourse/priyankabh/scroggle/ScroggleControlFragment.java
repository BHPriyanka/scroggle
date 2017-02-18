package edu.neu.madcourse.priyankabh.scroggle;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.priyankabh.R;
import edu.neu.madcourse.priyankabh.scroggle.ScroggleGameActivity;

/**
 * Created by priya on 2/15/2017.
 */

public class ScroggleControlFragment extends Fragment {

    private ScroggleFragment sFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.scroggle_fragment_control, container, false);
        final View pause = rootView.findViewById(R.id.pause_button);
        final View resume = rootView.findViewById(R.id.button_resume);
        final View quit = rootView.findViewById(R.id.quit_button);

        resume.setVisibility(View.INVISIBLE);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                ((ScroggleGameActivity) getActivity()).sFragment.onScrogglePause();
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.GONE);
                ((ScroggleGameActivity) getActivity()).sFragment.onScroggleResume();
            }
        });

        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((ScroggleGameActivity) getActivity()).sFragment.onScroggleQuit();
            }
        });

        final View mute = rootView.findViewById(R.id.mute);
        final View unmute = rootView.findViewById(R.id.unmute);
        unmute.setVisibility(View.INVISIBLE);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleGameActivity) getActivity()).sFragment.mute();
                 mute.setVisibility(View.GONE);
                unmute.setVisibility(View.VISIBLE);
            }
        });

        unmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleGameActivity) getActivity()).sFragment.mute();
                unmute.setVisibility(View.GONE);
                mute.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

}
