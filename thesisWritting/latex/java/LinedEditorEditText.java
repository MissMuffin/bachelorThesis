package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.AttributeSet;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;

public class LinedEditorEditText extends KeyboardlessEditText2 {

    public LinedEditorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set cursor visible and to beginning and request input focus
        //needed to update the parents size
        setCursorVisible(true);
        requestFocus();
        setSelection(0);

        float textSize = context.getResources().getDimension(R.dimen.row_editor_default_text_size);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH));
    }

    public Point getCursorPosition() {
    //https://stackoverflow.com/questions/5044342/how-to-get-cursor-position-x-y-in-edittext-android
        Layout layout = getLayout();
        if (layout != null) { //check if called before layout is inflated
            int pos = getSelectionStart();
            int line = layout.getLineForOffset(pos);
            int baseline = layout.getLineBaseline(line);
            int bl = (int)layout.getPrimaryHorizontal(pos);
            Point test = new Point(bl, baseline);
            return test;
        }
        return new Point(0,0);
    }
}