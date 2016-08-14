package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import de.muffinworks.knittingapp.util.Constants;

public class LineNumberTextView extends TextView {

    private int lines = 0;

    public LineNumberTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH));
    }

    public LineNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH));
    }

    public void updateLineNumbers(int lineCount) {
        lines = lineCount;
        String linesString = "1";
        for(int i = 1; i < lines; i++) {
            linesString += "\n" + (i+1);
        }
        setText(linesString);
    }

    private int measureLineNumberTextWidth() {
        return (int) getPaint().measureText(lines + "");
    }

    public int getExactWidth() {
        return measureLineNumberTextWidth() + getPaddingRight() + getPaddingLeft();
    }
}
