package de.muffinworks.knittingapp;

import android.graphics.Typeface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        EditText t = (EditText) findViewById(R.id.test);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/KnittingTest.ttf");
        t.setTypeface(font);
    }
}
