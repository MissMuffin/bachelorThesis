package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorActivity extends AppCompatActivity {

    private RowEditorLinearLayout mRowEditorContainer;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRowEditorContainer = (RowEditorLinearLayout) findViewById(R.id.row_editor_container);
        mEditText = mRowEditorContainer.getEditText();
        mEditText.callOnClick();
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

    @Override
    protected void onStart() {
        super.onStart();
        //needed to suppress soft keyboard on app switch (onStop & onPause)
        mRowEditorContainer.requestFocus();
    }

    /**
     * emulates the backspace key on the system's soft keyboard
     * @param view view that calls this method
     */
    public void onDelete(View view) {
        mRowEditorContainer.onDeletePressed();
    }

    /**
     * emulates the enter key on the system's soft keyboard
     * @param view view that calls this method
     */
    public void onEnter(View view) {
        mRowEditorContainer.onEnterPressed();
    }
}
