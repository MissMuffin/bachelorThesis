package de.muffinworks.knittingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.muffinworks.knittingapp.adapters.KeyboardGridAdapter;
import de.muffinworks.knittingapp.interfaces.GridEditorKeyListener;
import de.muffinworks.knittingapp.layouts.KeyboardLayout;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.views.GridEditorView;

/**
 * Created by Bianca on 11.07.2016.
 */
public class GridEditorActivity extends AppCompatActivity implements GridEditorKeyListener {

    private GridEditorView mGridEditor;
    private GridView mGridView;
    private boolean mIsDeleteActive = false;
    private KeyboardGridAdapter mKeyboardAdapter;
    private KeyboardLayout mDeleteButtonContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);

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
                showSetGridSizeDialog();
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

    private void showSetGridSizeDialog() {

        LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_set_grid_size, null);
        final int columnCount = mGridEditor.getColumns();
        final int rowCount = mGridEditor.getRows();

        final EditText columns = (EditText) content.findViewById(R.id.edittext_columns);
        columns.setText(Integer.toString(columnCount));

        final EditText rows = (EditText) content.findViewById(R.id.edittext_rows);
        rows.setText(Integer.toString(rowCount));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int newColumns = Integer.parseInt(columns.getText().toString());
                        int newRows = Integer.parseInt(rows.getText().toString());
                        if (newColumns != columnCount && newRows != rowCount) {
                            mGridEditor.setChartSize(newColumns, newRows);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setTitle("Strickmustergröße ändern")
                .setView(content)
                .create();

        dialog.show();
    }
}
