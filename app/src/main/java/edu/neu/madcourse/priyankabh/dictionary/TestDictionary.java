package edu.neu.madcourse.priyankabh.dictionary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.neu.madcourse.priyankabh.R;

/**
 * Created by priya on 1/24/2017.
 */

public class TestDictionary extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_test);


        EditText editText = (EditText) findViewById(R.id.word_entry);
        TextView textView = (TextView)findViewById(R.id.view_words);
        textView.setText(editText.getText());

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.word_entry);
                editText.setText("");
            }
        });
        Button ackButton = (Button) findViewById(R.id.ack_button);
        Button returnMenuButton = (Button) findViewById(R.id.menu_button);
    }


}
