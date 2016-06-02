package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Bianca on 02.06.2016.
 */
public class KnittingFontButton extends Button {

    public KnittingFontButton(Context context) {
        super(context);
        this.setTypeface(getKnittingTypeFace(context));
    }

    public KnittingFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(getKnittingTypeFace(context));
    }

    public KnittingFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(getKnittingTypeFace(context));
    }

    public KnittingFontButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTypeface(getKnittingTypeFace(context));
    }

    private Typeface getKnittingTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH);
    }
}
