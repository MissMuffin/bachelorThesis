package de.muffinworks.knittingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.views.GridEditorView;

public class ViewerActivity extends AppCompatActivity {


    private ImageButton mIncreaseRow;
    private ImageButton mDecreaseRow;
    private int mRows = 0;
    private TextView mRowText;
    private FrameLayout mEditorContainer;

    private GridEditorView mGridEditor;
    private RowEditorLinearLayout mRowEditor;
    private boolean mIsRowEditorActive = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        initCounter();
        initEditors();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_row_counter) {
            Toast.makeText(this, "Reset counter", Toast.LENGTH_SHORT).show();
            updateRowText(0);
            return true;
        } else if (id == R.id.switch_view_style) {
            Toast.makeText(this, "Switch view", Toast.LENGTH_SHORT).show();
            switchEditors();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      counter
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initCounter() {
        mRowText = (TextView) findViewById(R.id.row);
        updateRowText();

        mIncreaseRow = (ImageButton) findViewById(R.id.button_increase);
        mIncreaseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRowText(mRows+1);
            }
        });

        mDecreaseRow = (ImageButton) findViewById(R.id.button_decrease);
        mDecreaseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRowText(mRows-1);
            }
        });
    }

    private void updateRowText(int rows) {
        mRows = rows;
        mRowText.setText(Integer.toString(mRows));
    }

    private void updateRowText() {
        updateRowText(mRows);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      editor stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initEditors() {
        mEditorContainer = (FrameLayout) findViewById(R.id.editor_container);
        mGridEditor = new GridEditorView(this);
        mRowEditor = new RowEditorLinearLayout(this);
        //if we start with grid editor and switch, the layout height isn't set correctly?
        //works fine if the we start with row editor, will be ognored for now
        mEditorContainer.addView(mRowEditor);
    }

    private void switchEditors() {
        if (mIsRowEditorActive) {
            mEditorContainer.removeAllViews();
            mEditorContainer.addView(mRowEditor);
        } else {
            mEditorContainer.removeAllViews();
            mEditorContainer.addView(mGridEditor);
        }
        mIsRowEditorActive = !mIsRowEditorActive;
    }
}

