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
