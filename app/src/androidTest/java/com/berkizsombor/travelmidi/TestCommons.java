package com.berkizsombor.travelmidi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by berki on 2017. 04. 30..
 */

public class TestCommons {

    public static Idea createFakeIdea(String path) {
        List<String> tags = new ArrayList<>();
        tags.add("test");
        tags.add("filter");

        Idea i = new Idea("idea 1", tags, path);

        return i;
    }

    public static List<Idea> createIdeaList(String path) {
        List<Idea> l = new ArrayList<>();

        Idea i = createFakeIdea(path);
        l.add(i);

        return l;
    }
}
