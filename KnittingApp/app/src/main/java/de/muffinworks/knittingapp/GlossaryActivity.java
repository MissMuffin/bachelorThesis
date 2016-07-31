package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import de.muffinworks.knittingapp.views.adapters.GlossaryAdapter;

/**
 * Created by Bianca on 31.07.2016.
 */
public class GlossaryActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private ListView mGlossaryListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setTitle(R.string.activity_title_glossary);

        mGlossaryListView = (ListView) findViewById(R.id.glossary_listview);
        mGlossaryListView.setAdapter(new GlossaryAdapter(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
