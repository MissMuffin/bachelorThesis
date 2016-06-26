package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Bianca on 18.06.2016.
 */
public class LinearLayoutTestActivity extends AppCompatActivity {

    private RowEditorLinearLayout mRowEditorContainer;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRowEditorContainer = (RowEditorLinearLayout) findViewById(R.id.row_editor_container);
        mEditText = mRowEditorContainer.getEditText();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        init line numbers here because view hasn't been built before this point
        mRowEditorContainer.initLineNumbers();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * inserts the text of the clicked view (button) at the position of the cursor in the edittext
     * @param view view that calls this method
     */
    public void onButtonClick(View view) {
        int start = mEditText.getSelectionStart();
        CharSequence text = ((Button)view).getText();
        mEditText.getText().insert(start, text);
    }

    /**
     * emulates the backspace key on the system's soft keyboard
     * @param view view that calls this method
     */
    public void onDelete(View view) {
        String input = mEditText.getText().toString();
        String lastTwoChars = input.length() > 1 ? input.substring(input.length() - 1) : input;
        mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        if (lastTwoChars.equals("\n")) {
            mRowEditorContainer.decrementLineNumber();
        }
    }

    /**
     * emulates the enter key on the system's soft keyboard
     * @param view view that calls this method
     */
    public void onEnter(View view) {
        Toast.makeText(this, mEditText.getLineCount()+"", Toast.LENGTH_LONG).show();
        mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        mRowEditorContainer.incrementLineNumber();
    }
}
