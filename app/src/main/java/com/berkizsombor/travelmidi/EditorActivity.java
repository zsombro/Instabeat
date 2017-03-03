package com.berkizsombor.travelmidi;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    public static final int DEFAULT_BPM = 120;

    private EditorView editorView;
    private ImageButton playButton, stopButton, editButton;
    private Button bpmButton;

    private Idea idea;

    private MidiFile midi;
    private Tempo tempo;

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

        tempo = new Tempo();
        tempo.setBpm(DEFAULT_BPM);

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
            loadOrCreateMidiData();

            bpmButton.setText(String.format("%sBPM", tempo.getBpm()));
        } catch (IOException e) {
            finish();
        }
    }

    private void loadOrCreateMidiData() throws IOException {
        File f = new File(idea.getFileName());
        if (f.exists()) {
            midi = new MidiFile(new File(idea.getFileName()));
            // TODO: things to solve: read BPM, place every note on the editor
            loadExistingMidiFile();
        } else {
            midi = new MidiFile();
            // TODO: initialize BPM!!!
            setupNewMidiFile();
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

    private void setupNewMidiFile() {
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack1 = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        midi.addTrack(tempoTrack);
        midi.addTrack(noteTrack1);
    }

    private void loadExistingMidiFile() {
        MidiTrack tempoTrack = midi.getTracks().get(0);

        // get BPM for track
        for (MidiEvent e :
                tempoTrack.getEvents()) {
            if (e instanceof Tempo) {
                tempo = (Tempo) e;
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        File out = new File(idea.getFileName());
        try {
            midi.writeToFile(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBpmDialog() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_change_bpm);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.bpm_picker);
        np.setMaxValue(300);
        np.setMinValue(0);
        np.setValue((int) tempo.getBpm());
        // TODO: when BPM changes, it needs to be readjusted in the tempo track

        Button okButton = (Button) d.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newBpm = np.getValue();

                tempo.setBpm(newBpm);
                bpmButton.setText(String.format(getString(R.string.bpm), newBpm));

                d.dismiss();
            }
        });

        d.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
