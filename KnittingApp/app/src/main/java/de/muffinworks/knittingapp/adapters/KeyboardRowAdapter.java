package de.muffinworks.knittingapp.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.base.KeyboardAdapterBase;
import de.muffinworks.knittingapp.interfaces.GridEditorKeyListener;
import de.muffinworks.knittingapp.interfaces.RowEditorKeyListener;
import de.muffinworks.knittingapp.views.KnittingFontButton;
import de.muffinworks.knittingapp.views.RowEditText;

/**
 * Created by Bianca on 21.07.2016.
 */
public class KeyboardRowAdapter extends KeyboardAdapterBase {

    private RowEditorKeyListener mListener;

    public KeyboardRowAdapter(Context context, RowEditorKeyListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        KnittingFontButton key;
        if (convertView == null) {
            //can only do it this way if we want the style to be applied to the view
            key = (KnittingFontButton) inflater.inflate(R.layout.gridview_key, null);
        } else {
            key = (KnittingFontButton) convertView;
        }

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
                mListener.onKeyClicked(mCharacters[position]);
            }
        });

        return key;
    }
}
