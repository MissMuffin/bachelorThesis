package de.muffinworks.knittingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import de.muffinworks.knittingapp.fragments.PatternNameDialogFragment;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.adapters.PatternListAdapter;

public class PatternListActivity extends BaseActivity
        implements PatternNameDialogFragment.OnPatternNameInteractionListener {

    private ListView mPatternsList;
    private PatternListAdapter mAdapter;
    private FloatingActionButton mFab;

    private MenuItem mExportAllMenu = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_list);

        enableBackInActionBar(false);

        mPatternsList = (ListView) findViewById(R.id.patterns_list);
        mAdapter = new PatternListAdapter(this);
        mPatternsList.setAdapter(mAdapter);
        mPatternsList.setItemsCanFocus(true);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetNameDialog();
            }
        });

        requestExternalStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        checkExportAvailability();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pattern_list, menu);
        mExportAllMenu = menu.findItem(R.id.export_all);
        checkExportAvailability();
        return super.onCreateOptionsMenu(menu);
    }

    private void checkExportAvailability() {
        if (mExportAllMenu != null) {
            mExportAllMenu.setVisible(mStorage.listMetadataEntries().length > 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.import_pattern) {
            if (isExternalStoragePermissionGranted()) {
                importFile();
            } else {
                requestExternalStoragePermission();
            }
        } else if (id == R.id.export_all) {
            if (isExternalStoragePermissionGranted()) {
                exportAllPatterns();
            } else {
                requestExternalStoragePermission();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportAllPatterns() {
        try {
            mStorage.exportAll();
            showAlertDialog(getString(R.string.success_export_all, Constants.EXPORT_DIR));
        } catch (IOException e) {
            showAlertDialog(getString(R.string.error_export));
        }
    }

    private void importFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        startActivityForResult(intent, Constants.FILE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.FILE_PICKER_REQUEST_CODE) {
            if (data != null) {
                try {
                    final Pattern importedPattern = mStorage.loadFromFile(data.getData().getPath());
                    if (mStorage.checkPatternDuplicate(importedPattern)) {
                        showAlertDialog(getString(R.string.info_import_pattern_already_exists),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mStorage.save(importedPattern);
                                    }
                                });
                    } else {
                        mStorage.save(importedPattern);
                    }
                } catch (JsonSyntaxException e) {
                    showAlertDialog(getString(R.string.error_import_no_json));
                }
            }
        }
    }

    private void showSetNameDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PatternNameDialogFragment dialog = PatternNameDialogFragment.newInstance("");
        dialog.show(fm, getString(R.string.tag_dialog_fragment_set_name));
    }

    @Override
    public void onSetName(String name) {
        Pattern pattern = new Pattern();
        pattern.setName(name);
        String patternId = pattern.getId();
        mStorage.save(pattern);

        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
        intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
        startActivity(intent);
    }
}
