package com.example.magdalena.hangman;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static char BLANK = '_';

    private ImageView hangmanView;
    private TextView theWordView;
    private GridView keyboardView;
    private ButtonAdapter keyboardAdapter;
    private ArrayList<View> touchables;
    private String theWord;
    private String[] dictionary;
    private int wrongGuesses;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dictionary = getResources().getStringArray(R.array.dictionary);

        hangmanView = (ImageView) findViewById(R.id.hangman);

        theWordView = (TextView) findViewById(R.id.the_word);

        keyboardView = (GridView) findViewById(R.id.keyboard);

        if(savedInstanceState != null) {
            wrongGuesses = savedInstanceState.getInt("wrongGuesses");

            int id = getResources().getIdentifier("hangman"+wrongGuesses, "drawable", getPackageName());
            hangmanView.setImageResource(id);

            theWord = savedInstanceState.getString("theWord");
            theWordView.setText(savedInstanceState.getString("currentText"));

            resetKeyboard();

            keyboardView.post(new Runnable() {
                @Override
                public void run() {
                    for(View v : keyboardView.getTouchables()) {

                        Log.d("restoreState", "inside for");
                        if(v != null && v instanceof KeyButton) {
                            Log.d("restoreState", "not null Button! yay");
                            KeyButton b = (KeyButton) v;
                            Character tag = (Character) b.getTag();

                            Boolean enabled = (Boolean) savedInstanceState.getSerializable(tag.toString());

                            Log.d("restoreState", tag.toString() + ":" + enabled);

                            if (enabled != null ? !enabled : false) {
                                b.setEnabled(false);
                                b.setText("");
                                b.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                    }
                }
            });

        } else {
            startRound();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("currentText", theWordView.getText().toString());
        outState.putString("theWord", theWord);
        outState.putInt("wrongGuesses", wrongGuesses);

        for(int i=0; i<keyboardView.getCount(); i++) {
            View v = keyboardView.getChildAt(i);
            if(v != null && v instanceof KeyButton) {
                KeyButton b = (KeyButton) v;
                Character tag = (Character) b.getTag();
                if(tag != '>' && tag != ' ') {
                    outState.putSerializable(tag.toString(), b.isEnabled());
                }
            }
        }
    }

    private void startRound() {
        hangmanView.setImageResource(R.drawable.hangman0);

        int idx = new Random().nextInt(dictionary.length);
        theWord = dictionary[idx].toUpperCase();
        theWordView.setText(blankWord(theWord.length()));

        wrongGuesses = 0;

        resetKeyboard();
    }

    private String blankWord(int length){
        String blankWord = "";

        for(int i=0; i<length; i++){
            blankWord += BLANK;
        }
        return blankWord;
    }

    private void resetKeyboard() {
        keyboardAdapter = new ButtonAdapter(this);

        keyboardView.setAdapter(keyboardAdapter);

        keyboardAdapter.setOnButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyButton b = (KeyButton) v;
                Character tag = (Character) b.getTag();

                if(tag == '>') {
                    startRound();
                } else {
                    boolean correctGuess = theWord.indexOf(tag) != -1;

                    b.onClick(correctGuess);

                    if (correctGuess) {
                        String currentText = theWordView.getText().toString();
                        StringBuilder newTextBuilder = new StringBuilder();

                        for (int i = 0; i < theWord.length(); i++) {
                            if (theWord.charAt(i) == tag) {
                                newTextBuilder.append(theWord.charAt(i));
                            } else {
                                newTextBuilder.append(currentText.charAt(i));
                            }
                        }
                        String newText = newTextBuilder.toString();
                        theWordView.setText(newText);

                        if (newText.equalsIgnoreCase(theWord)) {
                            disableKeyboard();

                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.win,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } else {
                        wrongGuesses++;
                        int id = getResources().getIdentifier("hangman"+wrongGuesses, "drawable", getPackageName());
                        hangmanView.setImageResource(id);
                        if (wrongGuesses >= 10) {
                            disableKeyboard();

                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.lost,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
            }
        });
    }

    private void disableKeyboard() {
        for (View t : keyboardView.getTouchables()) {
            if (t instanceof KeyButton && (Character) t.getTag() != '>') {
                t.setEnabled(false);
            }
        }
    }

}
