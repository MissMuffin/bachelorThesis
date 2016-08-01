package de.muffinworks.knittingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;

import de.muffinworks.knittingapp.fragments.GridEditorFragment;
import de.muffinworks.knittingapp.fragments.GridSizeDialogFragment;
import de.muffinworks.knittingapp.fragments.PatternDeleteDialogFragment;
import de.muffinworks.knittingapp.fragments.PatternNameDialogFragment;
import de.muffinworks.knittingapp.fragments.RowEditorFragment;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 25.07.2016.
 */
public class EditorActivity extends BaseActivity
        implements  PatternNameDialogFragment.OnPatternNameInteractionListener,
                    PatternDeleteDialogFragment.OnPatternDeleteInteractionListener,
                    GridSizeDialogFragment.OnGridSizeInteractionListener {

    private FragmentManager mFragmentManager;
    private RowEditorFragment mRowEditorFragment;
    private GridEditorFragment mGridEditorFragment;
    private int mFragmentContainer = R.id.fragment_container;
    private MenuItem mMenuItemSetGridSize;

    private Pattern mPattern;
    private String mPatternId = null;

    private boolean mWasEdited = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        enableBackInActionBar(true);

        mPatternId = getIntent().getStringExtra(Constants.EXTRA_PATTERN_ID);

        if (mPatternId != null) {
            mPattern = mStorage.load(mPatternId);
            setActionBarTitle(mPattern.getName());
        }

        mRowEditorFragment = RowEditorFragment.getInstance(mPatternId);
        mGridEditorFragment = GridEditorFragment.getInstance(mPatternId);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fm = mFragmentManager.beginTransaction();
        fm.replace(mFragmentContainer, mRowEditorFragment);
        fm.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        mMenuItemSetGridSize = menu.findItem(R.id.set_size);
        mMenuItemSetGridSize.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set_size) {
            showSetSizeDialog();
        } else if (id == R.id.delete_pattern) {
            showDeletePatternDialog();
        } else if (id == R.id.switch_editor) {
            switchEditors();
        } else if (id == R.id.edit_pattern_name) {
            showEditNameDialog();
        } else if (id == R.id.save_pattern) {
            savePattern();
        } else if (id == R.id.open_glossary) {
            startActivity(new Intent(this, GlossaryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (wasPatternEdited()) {
            AlertDialog saveBeforeExitDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_pattern_save_changes))
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePattern();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        setResult(mWasEdited == false ? Activity.RESULT_CANCELED : Activity.RESULT_OK);
                        finish();
                    }
                })
                .create();
            saveBeforeExitDialog.show();
        } else {
            setResult(mWasEdited == false ? Activity.RESULT_CANCELED : Activity.RESULT_OK);
            super.onBackPressed();
        }
    }

    private void switchEditors() {
            savePattern();

            FragmentTransaction fm = mFragmentManager.beginTransaction();
        if (mRowEditorFragment.isVisible()) {
            fm.replace(mFragmentContainer, mGridEditorFragment);
            mMenuItemSetGridSize.setVisible(true);
        } else {
            fm.replace(mFragmentContainer, mRowEditorFragment);
            mMenuItemSetGridSize.setVisible(false);
        }
        fm.commit();
    }

    private boolean wasPatternEdited() {
        if (mRowEditorFragment.isVisible()) {
            return mRowEditorFragment.hasPatternChanged();
        } else {
            return mGridEditorFragment.hasPatternChanged();
        }
    }

    private void savePattern() {
        if (wasPatternEdited()) {
            if (mRowEditorFragment.isVisible()) {
                mRowEditorFragment.savePattern();
            } else {
                mGridEditorFragment.savePattern();
            }
            mWasEdited = true;
            mPattern = mStorage.load(mPatternId);
        }
    }

    @Override
    public void onSetChartSize(int columns, int rows) {
        mGridEditorFragment.setGridSize(columns, rows);
        savePattern();
    }

    public void showSetSizeDialog() {
        GridSizeDialogFragment dialog = GridSizeDialogFragment.newInstance(
                mPattern.getColumns(),
                mPattern.getRows());
        dialog.show(mFragmentManager, getString(R.string.tag_dialog_fragment_grid_size));
    }

    private void showEditNameDialog() {
        Pattern pattern = mStorage.load(mPatternId);
        PatternNameDialogFragment dialog = PatternNameDialogFragment.newInstance(pattern.getName());
        dialog.show(mFragmentManager, getString(R.string.tag_dialog_fragment_edit_name));
    }

    private void showDeletePatternDialog() {
        PatternDeleteDialogFragment dialog = PatternDeleteDialogFragment.newInstance(mPattern.getName());
        dialog.show(mFragmentManager, getString(R.string.tag_dialog_fragment_delete_pattern));
    }

    private void refreshFragmentData() {
        mGridEditorFragment.notifyDataChanged();
        mRowEditorFragment.notifyDataChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      row editor stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onNumPadClick(View view) {
        String num = ((Button)view).getText().toString();
        mRowEditorFragment.onNumPadClick(num);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      grid editor stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onDeleteToggled(View view) {
        mGridEditorFragment.onDeleteToggled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      interfaces
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSetName(String name) {
        mPattern.setName(name);
        mStorage.save(mPattern);
        setActionBarTitle(mPattern.getName());
        mWasEdited = true;
        refreshFragmentData();
    }


    @Override
    public void onConfirmDelete() {
        mStorage.delete(mPatternId);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.EXTRA_PATTERN_DELETED, true);
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }
}
