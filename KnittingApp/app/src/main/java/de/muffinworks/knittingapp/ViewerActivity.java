package de.muffinworks.knittingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_grid_style);
    }
}
