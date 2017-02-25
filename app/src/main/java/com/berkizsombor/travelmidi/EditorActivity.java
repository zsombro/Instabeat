package com.berkizsombor.travelmidi;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    private EditorView editorView;
    private ImageButton playButton, stopButton, editButton;
    private Button bpmButton;

    private Idea idea;

    private int bpm = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editorView = (EditorView) findViewById(R.id.editor);
        playButton = (ImageButton) findViewById(R.id.play);
        stopButton = (ImageButton) findViewById(R.id.stop);
        editButton = (ImageButton) findViewById(R.id.synth_settings);

        bpmButton = (Button) findViewById(R.id.bpm);

        idea = (Idea) getIntent().getSerializableExtra("idea");
        editorView.setIdea(idea);

        bpmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBpmDialog();
            }
        });

        editorView.setNotePressedListener(new OnNotePressedListener() {
            @Override
            public void onNotePressed(byte note) {
                // TODO: play the note that was pressed
                PdBase.sendNoteOn(0, note, 127);
                Toast.makeText(
                        getApplicationContext(),
                        "NOTE: " + Byte.toString(note),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        editorView.setNoteReleasedListener(new OnNoteReleasedListener() {
            @Override
            public void onNoteReleased(byte note) {
                PdBase.sendNoteOn(0, note, 0);
            }
        });

        editorView.setNotePlacedListener(new OnNotePlacedListener() {
            @Override
            public void OnNotePlaced(byte note, int pos) {
                // TODO: add this note to MIDI data AND play the note
            }
        });

        try {
            initPureData();
            loadPatch();
        } catch (IOException e) {
            finish();
        }
    }

    private void initPureData() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
    }

    private void loadPatch() throws IOException {
        File f = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.tm_core), f, true);

        File patch = new File(f, "tm_core.pd");
        PdBase.openPatch(patch.getAbsolutePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }

    private void showBpmDialog() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_change_bpm);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.bpm_picker);
        np.setMaxValue(300);
        np.setMinValue(0);
        np.setValue(bpm);

        Button okButton = (Button) d.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = np.getValue();
                bpmButton.setText(String.format(getString(R.string.bpm), bpm));

                d.dismiss();
            }
        });

        d.show();
    }
}
