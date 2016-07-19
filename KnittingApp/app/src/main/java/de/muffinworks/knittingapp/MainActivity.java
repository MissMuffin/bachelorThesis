package de.muffinworks.knittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButtonViewer;
    private Button mButtonEditor;
    private CoordinatorLayout mCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoord = (CoordinatorLayout) findViewById(R.id.coord);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mButtonViewer = (Button) findViewById(R.id.grid_editor);
        mButtonViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Snackbar.make(mCoord, "viewer", Snackbar.LENGTH_SHORT).show();
                Intent intent =  new Intent(MainActivity.this, GridEditorActivity.class);
                startActivity(intent);
            }
        });

        mButtonEditor = (Button) findViewById(R.id.line_editor);
        mButtonEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(mCoord, "editor", Snackbar.LENGTH_SHORT).show();
                Intent intent =  new Intent(MainActivity.this, RowEditorActivity.class);
                startActivity(intent);
            }
        });

//        mButtonViewer.callOnClick();

        //// TODO: 18.06.2016 remove
        Intent intent =  new Intent(MainActivity.this, LinearLayoutTestActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
