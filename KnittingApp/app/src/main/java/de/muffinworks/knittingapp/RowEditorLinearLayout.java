package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorLinearLayout extends LinearLayout {

    LineNumberTextView lineNumbers;
    KeyboardlessEditText2 editText;
    int lines; //first line starts at 0

    public RowEditorLinearLayout(Context context) {
        super(context);
    }

    public RowEditorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.row_editor, this, true);

        lineNumbers = (LineNumberTextView) findViewById(R.id.row_editor_line_numbers);
        editText = (KeyboardlessEditText2) findViewById(R.id.row_editor_edit_text);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight
    }

    public void initLineNumbers() {
        lineNumbers.initLineNumbers(editText.getLineCount());
    }

    public void incrementLineNumber() {
        lineNumbers.incrementLineNumber();
    }

    public void decrementLineNumber() {
        lineNumbers.decrementLineNumber();
    }

    public KeyboardlessEditText2 getEditText() {
        return editText;
    }
}
