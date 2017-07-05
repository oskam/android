package com.example.magdalena.hangman;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

/**
 * Created by Magdalena on 2017-03-19.
 */

public class KeyButton extends android.support.v7.widget.AppCompatButton {
    private Context context;

    public KeyButton(Context context) {
        super(context);
        this.context = context;
    }

    public void onClick(boolean correctGuess){
        this.setEnabled(false);
        this.setText("");
        this.setBackgroundColor(Color.TRANSPARENT);

        Toast.makeText(
                context,
                correctGuess ? R.string.correct_guess : R.string.wrong_guess,
                Toast.LENGTH_SHORT
        ).show();
    }
}
