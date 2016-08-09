package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 26.06.2016.
 */
public class LineNumberTextView extends TextView {

    private int lines = 0; //first line starts at 1


    public LineNumberTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH));
    }

    public LineNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.KNITTING_FONT_PATH));
    }

    /**
     * Method that adds line numbers to this textview. Used when being initialized with an editText
     * that already contains text
     * @param lineCount the number of lines the editText in the parent view contains
     */
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

    /**
     * Adds one line and the number of that line to this textView and increments lines variable
     */
    public void incrementLineNumber() {
        lines++;
        String currentLineNumbers = getText().toString();
        String newLineNumbers = "";
        newLineNumbers = currentLineNumbers + "\n" + (lines);
        setText(newLineNumbers);
    }

    /**
     * Removes last line from this textView and decrements lines variable
     */
    public void decrementLineNumber() {
        lines--;
        String currentLineNumbers = getText().toString();
        String newLineNumbers = "";
        int lastLine = currentLineNumbers.lastIndexOf("\n");
        newLineNumbers = currentLineNumbers.substring(0, lastLine);
        setText(newLineNumbers);
    }
}
