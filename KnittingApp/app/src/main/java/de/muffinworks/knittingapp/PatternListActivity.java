package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.muffinworks.knittingapp.adapters.PatternListAdapter;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListActivity extends AppCompatActivity {

    private ListView mPatternsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_list_activity);

        mPatternsList = (ListView) findViewById(R.id.patterns_list);
        mPatternsList.setAdapter(new PatternListAdapter(this));
    }
}
