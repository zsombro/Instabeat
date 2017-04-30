package com.berkizsombor.travelmidi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created by berki on 2017. 04. 29..
 */

public class IdeaTest {

    @Test
    public void generatesFileName() throws Exception {
        List<String> tags = new ArrayList<String>();

        Idea i = new Idea("idea 1", tags, "examplePath");

        assertEquals("examplePath\\idea1.mid", i.getFileName());
    }

    @Test
    public void generatesDefaultSettings() throws Exception {
        Idea idea = new Idea("idea");

        for (int i = 0; i < 4; i++) {
            assertNotNull(idea.getSynthSettings()[i]);
        }
    }
}
