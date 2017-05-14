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

import java.io.Serializable;

/**
 * Created by berki on 2017. 03. 16..
 */

// A Data Transfer Object (DTO) for transferring and storing synthesizer configurations
public class SynthSettings implements Serializable {

    // envelope filter
    private float attack = 0;
    private float decay = 100;
    private float sustain = 30;
    private float release = 100;

    public SynthSettings() {}

    public SynthSettings(float attack, float decay, float sustain, float release) {
        this.attack = attack;
        this.decay = decay;
        this.sustain = sustain;
        this.release = release;
    }

    public float getAttack() {
        return attack;
    }

    public void setAttack(float attack) {
        this.attack = attack;
    }

    public float getDecay() {
        return decay;
    }

    public void setDecay(float decay) {
        this.decay = decay;
    }

    public float getSustain() {
        return sustain;
    }

    public void setSustain(float sustain) {
        this.sustain = sustain;
    }

    public float getRelease() {
        return release;
    }

    public void setRelease(float release) {
        this.release = release;
    }
}
