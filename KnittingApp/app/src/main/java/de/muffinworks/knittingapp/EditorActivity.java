package de.muffinworks.knittingapp;

import android.graphics.Typeface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditorActivity extends AppCompatActivity {

    private EditText mEditTextPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mEditTextPattern = (EditText) findViewById(R.id.test);
        Typeface font = Typeface.createFromAsset(getAssets(), Constants.KNITTING_FONT_PATH);
        mEditTextPattern.setTypeface(font);
        /*called to make cursor appear and to focus the edittext*/
        mEditTextPattern.callOnClick();
    }

    /**
     * inserts the text of the clicked view (button) at the position of the cursor in the edittext
     * @param view
     */
    public void onButtonClick(View view) {
        int start = mEditTextPattern.getSelectionStart();
        CharSequence text = ((Button)view).getText();
        mEditTextPattern.getText().insert(start, text);
        //// TODO: 02.06.2016 extend edittext's width and remove wrap around & make edittext scrollable horizontally 
        //// TODO: 02.06.2016 make edittext zoomable
    }

    /**
     * emulates the backspace key on the system's soft keyboard
     * @param view
     */
    public void onDelete(View view) {
        mEditTextPattern.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    /**
     * emulates the enter key on the system's soft keyboard
     * @param view
     */
    public void onEnter(View view) {
        mEditTextPattern.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
    }
}
