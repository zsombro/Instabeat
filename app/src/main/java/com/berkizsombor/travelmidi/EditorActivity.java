/*

Copyright 2017 Berki Zoltán Zsombor

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.berkizsombor.travelmidi;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.util.MidiProcessor;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    public static final int DEFAULT_BPM = 120;
    public static final float MAX_SYNTH_VOLUME = 0.04f;
    public static final int INTENT_SYNTH_SETTINGS = 2;

    private EditorView editorView;
    private ImageButton playButton, stopButton, editButton;
    private Button bpmButton;
    private Button[] trackButtons;

    private Idea idea;
    private SynthSettings[] synthSettings;

    private MidiProcessor processor;
    private MidiFile midi;
    private Tempo tempo;
    MidiTrack noteTrack;

    private float volume = 0.03f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editorView = (EditorView) findViewById(R.id.editor);
        playButton = (ImageButton) findViewById(R.id.play);
        stopButton = (ImageButton) findViewById(R.id.stop);
        editButton = (ImageButton) findViewById(R.id.synth_settings);

        bpmButton = (Button) findViewById(R.id.bpm);

        trackButtons = new Button[EditorView.NUM_CHANNELS];

        // channel switching buttons
        for (int i = 0; i < EditorView.NUM_CHANNELS; i++) {
            final int j = i + 1;
            int id = getResources().getIdentifier("track" + j, "id", getPackageName());
            trackButtons[i] = (Button) findViewById(id);

            trackButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editorView.setChannel(j);
                }
            });
        }

        idea = (Idea) getIntent().getSerializableExtra("idea");
        editorView.setIdea(idea);

        // TODO this is not final! Extend file format, add it to save/load mechanism
        synthSettings = idea.getSynthSettings();
        /*for (int i = 0; i < EditorView.NUM_CHANNELS; i++) {
            synthSettings[i] = new SynthSettings();
        }*/

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
                PdBase.sendNoteOn(0, note, 127);

                /*if (BuildConfig.DEBUG) {
                    Toast.makeText(
                            getApplicationContext(),
                            "NOTE: " + Byte.toString(note),
                            Toast.LENGTH_SHORT)
                            .show();
                }*/
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
            public void OnNotePlaced(int channel, byte note, int pos) {
                // TODO: this is nice, but everything is a quarter note
                noteTrack.insertNote(channel, note, 127, pos * MidiFile.DEFAULT_RESOLUTION, 120);
            }
        });

        editorView.setNoteRemovedListener(new OnNoteRemovedListener() {
            @Override
            public void OnNoteRemoved(int channel, byte note, int pos) {
                MidiEvent delete = null;

                for (MidiEvent e :
                        noteTrack.getEvents()) {
                    if (e instanceof NoteOn) {
                        NoteOn n = (NoteOn) e;
                        if (n.getNoteValue() == note
                                && n.getTick() == MidiFile.DEFAULT_RESOLUTION * pos) {
                            delete = e;
                            break;
                        }
                    }
                }

                if (delete != null) {
                    noteTrack.removeEvent(delete);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processor = new MidiProcessor(midi);
                processor.registerEventListener(new MidiPdInterface(), MidiEvent.class);
                processor.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processor != null && processor.isRunning()) {
                    processor.stop();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditorActivity.this, SynthSettingsActivity.class);
                i.putExtra("settings", synthSettings[editorView.getChannel() - 1]);

                startActivityForResult(i, INTENT_SYNTH_SETTINGS);
            }
        });

        try {
            initPureData();
            loadPatch();
            loadOrCreateMidiData();

            bpmButton.setText(String.format("%dBPM", (int) tempo.getBpm()));
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
        if (!PdAudio.isRunning()) {
            int sampleRate = AudioParameters.suggestSampleRate();
            PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        }
    }

    private void loadPatch() throws IOException {
        File f = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.channel_separator), f, true);

        File patch = new File(f, getString(R.string.main_patch_name));
        PdBase.openPatch(patch.getAbsolutePath());

        SynthSettings s = synthSettings[editorView.getChannel() - 1];

        applySynthSettings(s, 1);
        applySynthSettings(s, 2);
        applySynthSettings(s, 3);
        applySynthSettings(s, 4);

        PdBase.sendFloat("volumeGlobal", volume);
    }

    private void applySynthSettings(SynthSettings s, int synthId) {
        PdBase.sendList("a", s.getAttack(), synthId);
        PdBase.sendList("d", s.getDecay(), synthId);
        PdBase.sendList("s", s.getSustain(), synthId);
        PdBase.sendList("r", s.getRelease(), synthId);
    }

    private void setupNewMidiFile() {
        MidiTrack tempoTrack = new MidiTrack();
        noteTrack = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        midi.addTrack(tempoTrack);
        midi.addTrack(noteTrack);
    }

    private void loadExistingMidiFile() {
        List<MidiTrack> tracks = midi.getTracks();

        MidiTrack tempoTrack = tracks.get(0);
        noteTrack = tracks.get(1);

        // get BPM for track
        for (MidiEvent e :
                tempoTrack.getEvents()) {
            if (e instanceof Tempo) {
                tempo = (Tempo) e;
            }
        }

        // load notes
        for (MidiEvent e :
                noteTrack.getEvents()) {
            if (e instanceof NoteOn) {
                NoteOn n = (NoteOn) e;

                if (n.getVelocity() != 0) {
                    editorView.placeNote(n.getChannel(),
                            (byte) n.getNoteValue(), (int) n.getTick() / MidiFile.DEFAULT_RESOLUTION);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_SYNTH_SETTINGS) {
            if (resultCode == RESULT_OK) {
                SynthSettings newSettings =
                        (SynthSettings) data.getSerializableExtra("new_settings");

                synthSettings[editorView.getChannel() - 1] = newSettings;
                applySynthSettings(newSettings, editorView.getChannel());
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // up
            volume = (volume <= MAX_SYNTH_VOLUME) ? volume += 0.01 : volume;

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // down
            volume = (volume >= 0) ? volume -= 0.01 : volume;
            return true;
        }
        else
            return super.onKeyUp(keyCode, event);
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
        saveMidiData();
        saveSynthSettings();

        Intent editResult = new Intent();
        editResult.putExtra("idea", idea);

        setResult(RESULT_OK, editResult);
        finish();
    }

    private void saveMidiData() {
        File out = new File(idea.getFileName());
        try {
            midi.writeToFile(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSynthSettings() {
        idea.setSynthSettings(synthSettings);
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
    protected void onDestroy() {
        super.onDestroy();
        PdAudio.release();
        PdBase.release();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public Idea getIdea() {
        return idea;
    }
}
