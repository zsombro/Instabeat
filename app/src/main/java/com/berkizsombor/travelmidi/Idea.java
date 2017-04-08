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
