package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;

public class KnittingFontButton extends Button {

    public KnittingFontButton(Context context) {
        super(context);
        init(context);
    }

    public KnittingFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);    }

    public KnittingFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);    }

    private void init(Context context) {
        setTypeface(getKnittingTypeFace(context));
    }

    private Typeface getKnittingTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH);
    }

    public void setActive(boolean mIsActive) {
        if (mIsActive) {
            setTextColor(getResources().getColor(R.color.colorPrimary, null));
        } else {
            setTextColor(getResources().getColor(R.color.keyboard_button_text_color, null));
        }
    }
}
