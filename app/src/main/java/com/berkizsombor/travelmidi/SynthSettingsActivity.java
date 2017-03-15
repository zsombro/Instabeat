package com.berkizsombor.travelmidi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SynthSettingsActivity extends AppCompatActivity {

    TextView settingsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synth_settings);

        settingsText = (TextView) findViewById(R.id.settingsText);
        int i = getIntent().getIntExtra("track", 1);

        settingsText.setText(String.format("%d", i));
    }
}
