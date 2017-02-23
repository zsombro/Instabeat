package com.berkizsombor.travelmidi;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

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
                Toast.makeText(
                        getApplicationContext(),
                        "NOTE: " + Byte.toString(note),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        editorView.setNotePlacedListener(new OnNotePlacedListener() {
            @Override
            public void OnNotePlaced(byte note, int pos) {
                // TODO: add this note to MIDI data AND play the note
            }
        });
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
