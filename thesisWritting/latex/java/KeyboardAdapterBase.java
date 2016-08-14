package de.muffinworks.knittingapp.views.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import de.muffinworks.knittingapp.util.Constants;

public abstract class KeyboardAdapterBase extends BaseAdapter {

    protected String [] mDescriptions;
    protected String[] mCharacters;
    protected static LayoutInflater inflater = null;
    protected Context mContext;
    protected Snackbar mSnackbar = null;

    public KeyboardAdapterBase(Context context) {

        mDescriptions = Constants.SYMBOL_DESCRIPTIONS;
        mCharacters = Constants.SYMBOLS;
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
