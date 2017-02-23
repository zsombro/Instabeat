package com.berkizsombor.travelmidi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int INTENT_CREATE_SONG = 1;

    ListView ideasListView;
    EditText filterEditText;

    List<Idea> ideaList;

    IdeaAdapter ideaAdapter;
    IdeaFileManager ifm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ideasListView = (ListView) findViewById(R.id.ideas);
        filterEditText = (EditText) findViewById(R.id.filter);

        ifm = new IdeaFileManager(this);
        ideaList = ifm.load();

        ideaAdapter = new IdeaAdapter(this, ideaList, ifm);
        ideasListView.setAdapter(ideaAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), NewIdeaActivity.class);
                startActivityForResult(i, INTENT_CREATE_SONG);

                Snackbar.make(view, "ADMIRAL SNACKBAR", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ideasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditorActivity(ideaList.get(position));
            }
        });

        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ideaAdapter.getFilter().filter(s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_CREATE_SONG) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String[] tags = data.getStringArrayExtra("tags");

                Idea i = new Idea(name, Arrays.asList(tags),
                        getApplicationContext().getFilesDir().toString());

                ideaList.add(i);
                ideaAdapter.notifyDataSetChanged();

                // save to file
                ifm.save(ideaList);

                // TODO: how do we give feedback on whether the draft was created successfully?
                launchEditorActivity(i);
            }
        }
    }

    private void launchEditorActivity(Idea i) {
        Intent editorIntent = new Intent(getApplicationContext(), EditorActivity.class);
        editorIntent.putExtra("idea", i);
        startActivity(editorIntent);
    }
}
