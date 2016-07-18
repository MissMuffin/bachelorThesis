package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Bianca on 26.06.2016.
 */
public class LineNumberTextView extends TextView {

    private int lines; //first line starts at 0


    public LineNumberTextView(Context context) {
        super(context);
    }

    public LineNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    private int measureLineNunberTextWidth() {
        return (int) getPaint().measureText(lines + 1 + "");
    }

    public int getExactWidth() {
        return measureLineNunberTextWidth() + getPaddingRight() + getPaddingLeft();
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
