package com.berkizsombor.travelmidi;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by berki on 2017. 04. 30..
 */

@MediumTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    Context appContext;

    MainActivity ma;
    View view;
    ListView lv;
    ListAdapter la;

    @Before
    public void CreateIdeas() {
        appContext = InstrumentationRegistry.getTargetContext();

        List<Idea> list = TestCommons.createIdeaList(appContext.getFilesDir().toString());

        IdeaFileManager ifm = new IdeaFileManager(appContext);
        ifm.save(list);

        ma = rule.getActivity();
        view = ma.findViewById(R.id.ideas);

        lv = (ListView) view;
        la = lv.getAdapter();
    }

    @Test
    public void FillsListViewWithProjects() throws Exception {

        assertNotNull(view);
        assertThat(view, instanceOf(ListView.class));

        assertThat(la.getCount(), greaterThan(0));
    }
}
