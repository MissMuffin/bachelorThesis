package de.muffinworks.knittingapp;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.FrameLayout;

import java.security.spec.PSSParameterSpec;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.fragments.GridEditorFragment;
import de.muffinworks.knittingapp.fragments.GridSizeDialogFragment;
import de.muffinworks.knittingapp.fragments.PatternDeleteDialogFragment;
import de.muffinworks.knittingapp.fragments.PatternNameDialogFragment;
import de.muffinworks.knittingapp.fragments.RowEditorFragment;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 25.07.2016.
 */
public class EditorActivity extends AppCompatActivity
        implements  PatternNameDialogFragment.OnPatternNameInteractionListener,
                    PatternDeleteDialogFragment.OnPatternDeleteInteractionListener {

    private static String TAG = "EditorActivity";

    private FragmentManager mFragmentManager;
    private RowEditorFragment mRowEditorFragment;
    private GridEditorFragment mGridEditorFragment;
    private int mFragmentContainer = R.id.fragment_container;
    private MenuItem mMenuItemSetGridSize;

    private PatternStorageService mService;
    private Pattern mPattern;
    private String mPatternId = null;
    private ActionBar mActionBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mPatternId = getIntent().getStringExtra(Constants.EXTRA_PATTERN_ID);
        mService = PatternStorageService.getInstance();
        mService.init(this);

        if (mPatternId != null) {
            mPattern = mService.load(mPatternId);
            mActionBar.setTitle(mPattern.getName());
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
            mGridEditorFragment.showSetSizeDialog();
            savePattern();
        } else if (id == R.id.delete_pattern) {
            showDeletePatternDialog();
        } else if (id == R.id.switch_editor) {
            switchEditors();
        } else if (id == R.id.edit_pattern_name) {
            showEditNameDialog();
        } else if (id == R.id.save_pattern) {
            savePattern();
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (wasPatternEdited()) {

            AlertDialog saveBeforeExitDialog = new AlertDialog.Builder(this)
                .setTitle("Änderungen speichern?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePattern();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                })
                .create();
            saveBeforeExitDialog.show();
        } else {
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
            mPattern = mService.load(mPatternId);
        }
    }

    private void showEditNameDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Pattern pattern = mService.load(mPatternId);
        PatternNameDialogFragment dialog = PatternNameDialogFragment.newInstance(pattern.getName());
        dialog.show(fm, "edit_name_dialog");
    }

    private void showDeletePatternDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PatternDeleteDialogFragment dialog = PatternDeleteDialogFragment.newInstance(mPattern.getName());
        dialog.show(fm, "delete_pattern_dialog");
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
        mService.save(mPattern);
        mActionBar.setTitle(mPattern.getName());
        refreshFragmentData();
    }


    @Override
    public void onConfirmDelete() {
        mService.delete(mPatternId);
        finish();
    }
}
