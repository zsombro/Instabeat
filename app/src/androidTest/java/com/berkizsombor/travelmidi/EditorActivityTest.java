package com.berkizsombor.travelmidi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by berki on 2017. 04. 30..
 */

@MediumTest
@RunWith(AndroidJUnit4.class)
public class EditorActivityTest {

    Idea idea;

    @Rule
    public ActivityTestRule<EditorActivity> rule
            = new ActivityTestRule<EditorActivity>(EditorActivity.class)
                {
                    @Override
                    protected Intent getActivityIntent() {
                        InstrumentationRegistry.getTargetContext();

                        idea = TestCommons.createFakeIdea(
                                        InstrumentationRegistry.getTargetContext().getFilesDir().toString());

                        Intent i;
                        i = new Intent(Intent.ACTION_MAIN);
                        i.putExtra("idea", idea);

                        return i;
                    }
                };

    Context appContext;
    EditorActivity ea;

    @Before
    public void PrepareActivity() {
        appContext = InstrumentationRegistry.getTargetContext();

        ea = rule.getActivity();
    }

    @Test
    public void LoadsProjectIntoEditor() throws Exception {
        assertEquals(ea.getIdea().getName(), idea.getName());
    }

    @Test
    public void SavesProjectIntoMidiFile() throws Exception {
        ea.onBackPressed();

        String file = ea.getIdea().getFileName();
        File f = new File(file);

        assertEquals(true, f.exists());
    }
}
