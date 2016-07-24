package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import de.muffinworks.knittingapp.views.adapters.PatternListAdapter;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListActivity extends AppCompatActivity {

    private ListView mPatternsList;
    private PatternListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_list);

        mPatternsList = (ListView) findViewById(R.id.patterns_list);
        mAdapter = new PatternListAdapter(this);
        mPatternsList.setAdapter(mAdapter);
        mPatternsList.setItemsCanFocus(true);

//        mPatternsList.setOnItemClickListener(new PatternListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String patternId = ((Metadata)mAdapter.getItem(position)).getId();
//                Intent intent = new Intent(PatternListActivity.this, ViewerActivity.class);
//                intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
//                startActivity(intent);
//            }
//        });
    }
}