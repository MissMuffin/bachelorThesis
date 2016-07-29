package de.muffinworks.knittingapp.views.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.views.KnittingFontButton;

/**
 * Created by Bianca on 21.07.2016.
 */
public class KeyboardTypingAdapter extends KeyboardAdapterBase {

    public interface RowEditorKeyListener {
        void onKeyClicked(String key);
    }

    private RowEditorKeyListener mListener;

    public KeyboardTypingAdapter(Context context, RowEditorKeyListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        KnittingFontButton key;
        if (convertView == null) {
            //can only do it this way if we want the style to be applied to the view
            key = (KnittingFontButton) inflater.inflate(R.layout.view_grid_key, null);
        } else {
            key = (KnittingFontButton) convertView;
        }

        key.setText(mCharacters[position]);
        key.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSnackbar = Snackbar.make(v, "clicked " + mDescriptions[position], Snackbar.LENGTH_SHORT)
                        .setAction(R.string.dialog_ok, new View.OnClickListener() {
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
