package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import de.muffinworks.knittingapp.views.adapters.KeyboardRowAdapter;
import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorActivity extends AppCompatActivity implements KeyboardRowAdapter.RowEditorKeyListener {

    private RowEditorLinearLayout mRowEditorContainer;
    private EditText mEditText;
    private GridView mKeyboardGridview;
    private Pattern mPattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_row);

        mRowEditorContainer = (RowEditorLinearLayout) findViewById(R.id.row_editor_container);
        mEditText = mRowEditorContainer.getEditText();

        mKeyboardGridview = (GridView) findViewById(R.id.keyboard_gridview);
        mKeyboardGridview.setAdapter(new KeyboardRowAdapter(this, this));

        String patternId = getIntent().getStringExtra(Constants.EXTRA_PATTERN_ID);
        if (patternId != null) {
            PatternStorageService storageService = PatternStorageService.getInstance();
            storageService.init(this);
            mPattern = storageService.load(patternId);
            mRowEditorContainer.setPattern(mPattern.getPatternRows());
        }

        mEditText.callOnClick();
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

    public void onNumPadClick(View view) {
        int start = mEditText.getSelectionStart();
        String num = ((Button)view).getText().toString();
        mEditText.getText().insert(start, num);
    }

    @Override
    public void onKeyClicked(String key) {
        int start = mEditText.getSelectionStart();
        mEditText.getText().insert(start, key);
    }
}
