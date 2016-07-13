package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.muffinworks.knittingapp.views.GridEditorView;

/**
 * Created by Bianca on 11.07.2016.
 */
public class GridEditorActivity extends AppCompatActivity {

    private GridEditorView mGrid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);
        mGrid = (GridEditorView) findViewById(R.id.grid);
    }

    /**
     * inserts the text of the clicked view (button) at the position of the cursor in the edittext
     * @param view
     */
    public void onButtonClick(View view) {

    }
}
