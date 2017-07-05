package com.example.magdalena.hangman;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Magdalena on 2017-03-19.
 */

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private View.OnClickListener mOnButtonClick;

    public ButtonAdapter(Context c) {
        mContext = c;
    }

    public void setOnButtonClickListener(View.OnClickListener listener) {
        mOnButtonClick = listener;
    }

    @Override
    public int getCount() {
        return mButtons.length;
    }

    @Override
    public Object getItem(int position) {
        return mButtons[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KeyButton btn;
        if (convertView == null) {
            btn = new KeyButton(mContext);

            if (mButtons[position] == ' ') {
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.TRANSPARENT);
            } else  {
                btn.setOnClickListener(mOnButtonClick);
            }

            btn.setTag(mButtons[position]);
        } else {
            btn = (KeyButton) convertView;
        }

        btn.setText("" + mButtons[position]);

        return btn;
    }


    private char[] mButtons = "ABCDEFGHIJKLMNOPQRSTUVWXYZ >".toCharArray();
}
