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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.scroggle_fragment_control, container, false);
        View pause = rootView.findViewById(R.id.pause_button);
        View restart = rootView.findViewById(R.id.button_resume);
        View quit = rootView.findViewById(R.id.quit_button);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleGameActivity) getActivity()).onScrogglePause();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScroggleGameActivity) getActivity()).onScroggleResume();
            }
        });

        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getActivity().finish();
            }
        });
        return rootView;
    }

}
