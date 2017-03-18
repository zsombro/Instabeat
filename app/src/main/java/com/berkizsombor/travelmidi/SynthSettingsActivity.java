package com.berkizsombor.travelmidi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SynthSettingsActivity extends AppCompatActivity {

    TextView settingsText;
    SeekBar attackSeek, decaySeek, sustainSeek, releaseSeek;

    SynthSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synth_settings);

        attackSeek = (SeekBar) findViewById(R.id.attack_seeker);
        decaySeek = (SeekBar) findViewById(R.id.decay_seeker);
        sustainSeek = (SeekBar) findViewById(R.id.sustain_seeker);
        releaseSeek = (SeekBar) findViewById(R.id.release_seeker);

        settings = (SynthSettings) getIntent().getSerializableExtra("settings");

        attackSeek.setProgress((int) settings.getAttack());
        decaySeek.setProgress((int) settings.getDecay());
        sustainSeek.setProgress((int) settings.getSustain());
        releaseSeek.setProgress((int) settings.getRelease());
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        SynthSettings newSettings = new SynthSettings(attackSeek.getProgress(),
                decaySeek.getProgress(), sustainSeek.getProgress(), releaseSeek.getProgress());
        result.putExtra("new_settings", newSettings);

        setResult(RESULT_OK, result);
        finish();
    }
}
