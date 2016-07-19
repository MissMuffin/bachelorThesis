package de.muffinworks.knittingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewerActivity extends AppCompatActivity {


    private ImageButton mIncreaseRow;
    private ImageButton mDecreaseRow;
    private int mRows = 0;
    private TextView mRowText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

