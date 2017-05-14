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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zsombor on 2017. 01. 30..
 */

public class Idea implements Serializable {
    private String name;
    private String fileName;
    private List<String> tags;
    private SynthSettings[] synthSettings;

    public Idea(String name, List<String> tags, String filePath) {
        this.name = name;
        this.fileName = new File(filePath, generateFileName(name)).getPath();
        this.tags = tags;
        GenerateDefaultSynthSettings();
    }

    public Idea(String name) {
        this.name = name;
        this.tags = new ArrayList<String>();
        GenerateDefaultSynthSettings();
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getFileName() {
        return fileName;
    }

    public SynthSettings[] getSynthSettings() {
        return synthSettings;
    }

    public void setSynthSettings(SynthSettings[] synthSettings) {
        this.synthSettings = synthSettings;
    }

    private String generateFileName(String name) {
        return name.replace(" ", "") + ".mid";
    }

    private void GenerateDefaultSynthSettings() {
        synthSettings = new SynthSettings[4];

        for (int i = 0; i < 4; i++) {
            synthSettings[i] = new SynthSettings();
        }
    }
}
