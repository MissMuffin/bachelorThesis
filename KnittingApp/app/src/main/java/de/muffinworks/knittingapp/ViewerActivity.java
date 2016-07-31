package de.muffinworks.knittingapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.PatternGridView;

public class ViewerActivity extends AppCompatActivity {

    private static final String TAG = "ViewerActivty";

    private ImageButton mIncreaseRow;
    private ImageButton mDecreaseRow;
    private int mCurrentRow = 1;
    private TextView mRowText;
    private FrameLayout mEditorContainer;

    private PatternGridView mGridEditor;
    private RowEditorLinearLayout mRowEditor;
    private boolean mIsRowEditorActive = true;

    private PatternStorage mService;
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
            mService = PatternStorage.getInstance();
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
        } else if (id == R.id.open_editor) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra(Constants.EXTRA_PATTERN_ID, mPattern.getId());
            startActivityForResult(intent, Constants.REQUEST_CODE_EDITOR);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_EDITOR) {
            if (resultCode == Activity.RESULT_OK) {
                //user changed pattern and saved -> viewer needs to refresh data
                mPattern = mService.load(mPattern.getId());
                mRowEditor.setPattern(mPattern.getPatternRows());
                if (mGridEditor != null) {
                    mGridEditor.setPattern(mPattern.getPatternRows());
                }
                mActionBar.setTitle(mPattern.getName());
            } else  if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    boolean wasPattenDeleted = data.getBooleanExtra(Constants.EXTRA_PATTERN_DELETED, false);
                    if (wasPattenDeleted) {
                        finish();
                    }
                }
            }
        }
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
        int maxRows = mPattern == null ? Constants.DEFAULT_ROWS : mPattern.getRows();
        mCurrentRow = Math.min(Math.max(rows, 1), maxRows);
        mRowText.setText(Integer.toString(mCurrentRow));
        if (mPattern != null) {
            mPattern.setCurrentRow(mCurrentRow);
            mService.save(mPattern);
        }
        if (mGridEditor != null) {
            mGridEditor.setCurrentRow(mCurrentRow);
        }
    }

    private void updateRowCounter() {
        if (mPattern != null) {
            mCurrentRow = mPattern.getCurrentRow();
        }
        updateRowCounter(mCurrentRow);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      editor stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initEditors() {
        mEditorContainer = (FrameLayout) findViewById(R.id.editor_container);

        mGridEditor = new PatternGridView(this);
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

