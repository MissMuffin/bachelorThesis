package de.muffinworks.knittingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.PatternGridView;

public class ViewerActivity extends BaseActivity {

    private int mCurrentRow = 1;
    private TextView mRowText;
    private FrameLayout mPatternContainer;

    private PatternGridView mGridPattern;
    private RowEditorLinearLayout mRowPattern;
    private boolean mIsRowFormatActive = false;

    private Pattern mPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        enableBackInActionBar(true);
        String patternId = getIntent().getStringExtra(Constants.EXTRA_PATTERN_ID);
        if (patternId != null) {
            mPattern = mStorage.load(patternId);
            setActionBarTitle(mPattern.getName());
        }
        initEditors();
        initCounter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_row_counter) {
            updateRowCounter(1);
            mGridPattern.scrollCurrentRowToCenter();
            return true;
        } else if (id == R.id.switch_view_style) {
            switchEditors();
            return true;
        } else if (id == R.id.scroll_current_row_to_center) {
            mGridPattern.scrollCurrentRowToCenter();
        } else if (id == R.id.open_editor) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra(Constants.EXTRA_PATTERN_ID, mPattern.getId());
            startActivityForResult(intent, Constants.REQUEST_CODE_EDITOR);
        } else if (id == R.id.open_glossary) {
            Intent intent = new Intent(this, GlossaryActivity.class);
            startActivity(intent);
        } else if (id == R.id.export_pattern) {
            exportPattern();
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportPattern() {
        try {
            mStorage.export(mPattern.getId());
            showAlertDialog(getString(R.string.success_export_pattern, Constants.EXPORT_DIR));
        } catch (IOException e) {
            showAlertDialog(getString(R.string.error_export));
        }
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
                mPattern = mStorage.load(mPattern.getId());
                mRowPattern.setPattern(mPattern.getPatternRows());
                if (mGridPattern != null) {
                    mGridPattern.setPattern(mPattern.getPatternRows());
                }
                setActionBarTitle(mPattern.getName());
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

    private void initCounter() {
        mRowText = (TextView) findViewById(R.id.row);
        updateRowCounter();

        ImageButton mIncreaseRow = (ImageButton) findViewById(R.id.button_increase);
        mIncreaseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRowCounter(mCurrentRow +1);
            }
        });

        ImageButton mDecreaseRow = (ImageButton) findViewById(R.id.button_decrease);
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
            mStorage.save(mPattern);
        }
        if (mGridPattern != null) {
            mGridPattern.setCurrentRow(mCurrentRow);
        }
    }

    private void updateRowCounter() {
        if (mPattern != null) {
            mCurrentRow = mPattern.getCurrentRow();
        }
        updateRowCounter(mCurrentRow);
    }

    private void initEditors() {
        mPatternContainer = (FrameLayout) findViewById(R.id.editor_container);

        mGridPattern = new PatternGridView(this);
        mGridPattern.setCanBeEdited(false);
        mGridPattern.setPattern(mPattern.getPatternRows());

        mRowPattern = new RowEditorLinearLayout(this);
        mRowPattern.disableEditable();

        mPatternContainer.addView(mGridPattern);
    }

    private void switchEditors() {
        if (!mIsRowFormatActive) {
            mPatternContainer.removeAllViews();
            mPatternContainer.addView(mRowPattern);
            mRowPattern.setPattern(mPattern.getPatternRows());
        } else {
            mPatternContainer.removeAllViews();
            mPatternContainer.addView(mGridPattern);
            mGridPattern.setPattern(mPattern.getPatternRows());
        }
        mIsRowFormatActive = !mIsRowFormatActive;
    }
}

