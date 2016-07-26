package de.muffinworks.knittingapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.GridEditorView;

public class ViewerActivity extends AppCompatActivity {

    private static final String TAG = "ViewerActivty";

    private ImageButton mIncreaseRow;
    private ImageButton mDecreaseRow;
    private int mCurrentRow = 1;
    private TextView mRowText;
    private FrameLayout mEditorContainer;

    private GridEditorView mGridEditor;
    private RowEditorLinearLayout mRowEditor;
    private boolean mIsRowEditorActive = true;

    private PatternStorageService mService;
    private Pattern mPattern;

    private ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        initEditors();

        String patternId = getIntent().getStringExtra(Constants.EXTRA_PATTERN_ID);
        if (patternId != null) {
            mService = PatternStorageService.getInstance();
            mService.init(this);
            mPattern = mService.load(patternId);
            mRowEditor.setPattern(mPattern.getPatternRows());
            mActionBar.setTitle(mPattern.getName());
        }

        initCounter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_row_counter) {
            updateRowCounter(1);
            mGridEditor.scrollCurrentRowToCenter();
            return true;
        } else if (id == R.id.switch_view_style) {
            switchEditors();
            return true;
        } else if (id == R.id.scroll_current_row_to_center) {
            mGridEditor.scrollCurrentRowToCenter();
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      counter
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initCounter() {
        mRowText = (TextView) findViewById(R.id.row);
        updateRowCounter();

        mIncreaseRow = (ImageButton) findViewById(R.id.button_increase);
        mIncreaseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRowCounter(mCurrentRow +1);
            }
        });

        mDecreaseRow = (ImageButton) findViewById(R.id.button_decrease);
        mDecreaseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRowCounter(mCurrentRow -1);
            }
        });
    }

    private void updateRowCounter(int rows) {
        int maxRows = mPattern == null ? Constants.DEFAULT_ROWS_SIZE : mPattern.getRows();
        mCurrentRow = Math.min(Math.max(rows, 1), maxRows);
        mRowText.setText(Integer.toString(mCurrentRow));

        if (mPattern != null) {
            mPattern.setCurrentRow(mCurrentRow);
            mService.save(mPattern);
        }

        refreshViews();
    }

    private void updateRowCounter() {
        if (mPattern != null) {
            mCurrentRow = mPattern.getCurrentRow();
        }
        updateRowCounter(mCurrentRow);
    }

    private void refreshViews() {
        if (mGridEditor != null) {
            mGridEditor.setCurrentRow(mCurrentRow);
        }
        if (mRowEditor != null) {
            mRowEditor.setCurrentRow(mCurrentRow);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      editor stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initEditors() {
        mEditorContainer = (FrameLayout) findViewById(R.id.editor_container);

        mGridEditor = new GridEditorView(this);
        mGridEditor.setCanBeEdited(false);

        mRowEditor = new RowEditorLinearLayout(this);
        mRowEditor.setCanBeEdited(false);

        //if we start with grid editor and switch, the layout height isn't set correctly?
        //works fine if the we start with row editor, will be ognored for now
        mEditorContainer.addView(mRowEditor);
    }

    private void switchEditors() {
        if (!mIsRowEditorActive) {
            mEditorContainer.removeAllViews();
            mEditorContainer.addView(mRowEditor);
        } else {
            mEditorContainer.removeAllViews();
            mEditorContainer.addView(mGridEditor);
            mGridEditor.setPattern(mPattern.getPatternRows());
        }
        mIsRowEditorActive = !mIsRowEditorActive;
    }
}

