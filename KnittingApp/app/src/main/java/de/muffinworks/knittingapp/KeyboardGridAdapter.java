package de.muffinworks.knittingapp;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.muffinworks.knittingapp.interfaces.GridEditorKeyListener;
import de.muffinworks.knittingapp.views.KnittingFontButton;

/**
 * Created by Bianca on 20.07.2016.
 */
public class KeyboardGridAdapter extends BaseAdapter {

    private String [] mDescriptions;
    private String[] mCharacters;
    private int mActiveKeyPosition = -1;
    private static LayoutInflater inflater = null;
    private Context mContext;
    private Snackbar mSnackbar = null;
    private GridEditorKeyListener mListener = null;


    public KeyboardGridAdapter(
            Context context,
            String[] descriptions,
            String[] characters,
            GridEditorKeyListener listener) {

        mDescriptions = descriptions;
        mCharacters = characters;
        mContext = context;
        mListener = listener;

        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mCharacters.length;
    }

    @Override
    public Object getItem(int position) {
        return mCharacters[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDeleteActive(boolean active) {
        if (active) {
            mActiveKeyPosition = -1;
            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        KnittingFontButton key;
        if (convertView == null) {
            //can only do it this way if we want the style to be applied to the view
            key = (KnittingFontButton) inflater.inflate(R.layout.grid_key, null);
        } else {
            key = (KnittingFontButton) convertView;
        }

        key.setActive(mActiveKeyPosition == position);
        key.setText(mCharacters[position]);
        key.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSnackbar = Snackbar.make(v, "clicked " + mDescriptions[position], Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSnackbar.dismiss();
                            }
                        });
                mSnackbar.show();
                return true;
            }
        });
        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActiveKeyPosition = position;
                mListener.onKeyToggled(mCharacters[position]);
                notifyDataSetChanged();
            }
        });

        return key;
    }
}