package com.berkizsombor.travelmidi;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.util.MidiEventListener;

import org.puredata.core.PdBase;

/**
 * Created by berki on 2017. 03. 05..
 */

public class MidiPdInterface implements MidiEventListener {

    public MidiPdInterface() {
    }

    @Override
    public void onStart(boolean fromBeginning) {

    }

    @Override
    public void onEvent(MidiEvent event, long ms) {
        if (event instanceof NoteOn) {
            NoteOn n = (NoteOn) event;
            PdBase.sendNoteOn(n.getChannel(), n.getNoteValue(), n.getVelocity());
        }
    }

    @Override
    public void onStop(boolean finished) {

    }
}
