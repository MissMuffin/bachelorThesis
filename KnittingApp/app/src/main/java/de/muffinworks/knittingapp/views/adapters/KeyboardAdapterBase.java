package de.muffinworks.knittingapp.views.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 21.07.2016.
 */
public abstract class KeyboardAdapterBase extends BaseAdapter {

    protected String [] mDescriptions;
    protected String[] mCharacters;
    protected static LayoutInflater inflater = null;
    protected Context mContext;
    protected Snackbar mSnackbar = null;

    public KeyboardAdapterBase(Context context) {

        mDescriptions = Constants.KEY_DESCRIPTIONS;
        mCharacters = Constants.KEY_STRINGS;
        mContext = context;

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
}
