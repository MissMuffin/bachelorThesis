package de.muffinworks.knittingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import de.muffinworks.knittingapp.fragments.PatternNameDialogFragment;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.adapters.PatternListAdapter;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListActivity extends BaseActivity
        implements PatternNameDialogFragment.OnPatternNameInteractionListener {

    private ListView mPatternsList;
    private PatternListAdapter mAdapter;
    private FloatingActionButton mFab;

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
