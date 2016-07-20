package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import de.muffinworks.knittingapp.interfaces.GridEditorKeyListener;
import de.muffinworks.knittingapp.layouts.KeyboardLayout;
import de.muffinworks.knittingapp.views.GridEditorView;
import de.muffinworks.knittingapp.views.KnittingFontButton;

/**
 * Created by Bianca on 11.07.2016.
 */
public class GridEditorActivity extends AppCompatActivity implements GridEditorKeyListener {

    private GridEditorView mGridEditor;
    private GridView mGridView;
    private boolean mIsDeleteActive = false;
    private KeyboardGridAdapter mKeyboardAdapter;
    private KeyboardLayout mDeleteButtonContainer;

    public static String [] descriptions = {
            "q desc",
            "w desc",
            "e desc",
            "r desc",
            "t desc",
            "y desc",
            "u desc",
            "i desc",
            "o desc",
            "p desc",
            "a desc",
            "s desc",
            "d desc",
            "f desc",
            "g desc",
            "h desc",
            "j desc",
            "k desc",
            "l desc",
            "z desc",
    };
    public static char [] characters = {
        'q',
        'w',
        'e',
        'r',
        't',
        'y',
        'u',
        'i',
        'o',
        'p',
        'a',
        's',
        'd',
        'f',
        'g',
        'h',
        'j',
        'k',
        'l',
        'z',
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);

        mGridEditor = (GridEditorView) findViewById(R.id.grid);
        mDeleteButtonContainer = (KeyboardLayout) findViewById(R.id.grid_delete_button_container);

        mGridView = (GridView) findViewById(R.id.keyboard_gridview);
        mKeyboardAdapter = new KeyboardGridAdapter(this, descriptions, characters, this);
        mGridView.setAdapter(mKeyboardAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_size:
            //open dialog
                // TODO: 20.07.2016 implement 
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      keyboard
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onKeyToggled(char key) {
        Log.i("mm", "toggled " + key);
        setDeleteActive(false);

    }

    public void onDeleteToggled(View view) {
        setDeleteActive(!mIsDeleteActive);
    }

    private void setDeleteActive(boolean active) {
        mIsDeleteActive = active;
        mKeyboardAdapter.setDeleteActive(mIsDeleteActive);
        if (mIsDeleteActive) {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.red_500, null));
        } else {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        }
    }
}
