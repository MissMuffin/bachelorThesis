package de.muffinworks.knittingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import de.muffinworks.knittingapp.fragments.GridSizeDialogFragment;
import de.muffinworks.knittingapp.views.adapters.KeyboardGridAdapter;
import de.muffinworks.knittingapp.layouts.KeyboardLayout;
import de.muffinworks.knittingapp.views.GridEditorView;

/**
 * Created by Bianca on 11.07.2016.
 */
public class GridEditorActivity extends AppCompatActivity
        implements  KeyboardGridAdapter.GridEditorKeyListener,
        GridSizeDialogFragment.OnGridSizeInteractionListener {

    private GridEditorView mGridEditor;
    private GridView mGridView;
    private boolean mIsDeleteActive = false;
    private KeyboardGridAdapter mKeyboardAdapter;
    private KeyboardLayout mDeleteButtonContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_grid);

        mGridEditor = (GridEditorView) findViewById(R.id.grid);
        mDeleteButtonContainer = (KeyboardLayout) findViewById(R.id.grid_delete_button_container);

        mGridView = (GridView) findViewById(R.id.keyboard_gridview);
        mKeyboardAdapter = new KeyboardGridAdapter(this, this);
        mGridView.setAdapter(mKeyboardAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grid_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_size:
                showDialog();
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
    public void onKeyToggled(String key) {
        Log.i("mm", "toggled " + key);
        setDeleteActive(false);
        mGridEditor.setSelectedKey(key);
    }

    public void onDeleteToggled(View view) {
        setDeleteActive(!mIsDeleteActive);
    }

    private void setDeleteActive(boolean active) {
        mIsDeleteActive = active;
        mKeyboardAdapter.setDeleteActive(mIsDeleteActive);
        mGridEditor.setDeleteActive(mIsDeleteActive);
        if (mIsDeleteActive) {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.red_500, null));
        } else {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      dialog
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void showDialog() {
        FragmentManager fm = getSupportFragmentManager();
        GridSizeDialogFragment dialog = GridSizeDialogFragment.newInstance(mGridEditor.getColumns(), mGridEditor.getRows());
        dialog.show(fm, "set size dialog fragment");
    }

    @Override
    public void onSetChartSize(int columns, int rows) {
        mGridEditor.setChartSize(columns, rows);
    }
}
