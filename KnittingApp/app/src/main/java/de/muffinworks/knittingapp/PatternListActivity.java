package de.muffinworks.knittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import de.muffinworks.knittingapp.fragments.PatternNameDialogFragment;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Metadata;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.adapters.PatternListAdapter;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListActivity extends AppCompatActivity implements PatternNameDialogFragment.OnPatternNameInteractionListener {

    private ListView mPatternsList;
    private PatternListAdapter mAdapter;
    private FloatingActionButton mFab;
    private PatternStorageService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_list);

        mPatternsList = (ListView) findViewById(R.id.patterns_list);
        mAdapter = new PatternListAdapter(this);
        mPatternsList.setAdapter(mAdapter);
        mPatternsList.setItemsCanFocus(true);

        mService = PatternStorageService.getInstance();
        mService.init(this);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show pattern name dialog
                showDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void showDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PatternNameDialogFragment dialog = PatternNameDialogFragment.newInstance("");
        dialog.show(fm, "pattern name dialog fragment");
    }

    @Override
    public void setName(String name) {
        Pattern pattern = new Pattern();
        pattern.setName(name);
        String patternId = pattern.getId();
        mService.save(pattern);

        Intent intent = new Intent(this, RowEditorActivity.class);
        intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
        startActivity(intent);
    }
}
