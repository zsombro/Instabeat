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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int INTENT_CREATE_SONG = 1;
    private final int INTENT_SAVE_SONG = 2;

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
            }
        });

        ideasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditorActivity(ideaList.get(position));
            }
        });

        ideasListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Export project")
                            .setItems(R.array.export_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: exportToExternalStorage(
                                                (Idea) ideasListView.getItemAtPosition(position));
                                                break;
                                    }
                                }
                            })
                    .create().show();

                return true;
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

                // does a project with this name already exist?
                boolean alreadyExists = false;
                Iterator<Idea> itr = ideaList.iterator();
                while (itr.hasNext() && !alreadyExists) {
                    if (itr.next().getName().equals(name))
                        alreadyExists = true;
                }

                if (!alreadyExists) {
                    Idea i = new Idea(name, Arrays.asList(tags),
                            getApplicationContext().getFilesDir().toString());

                    ideaList.add(i);
                    ideaAdapter.notifyDataSetChanged();

                    // save to file
                    ifm.save(ideaList);

                    launchEditorActivity(i);
                } else {
                    // error message
                    new AlertDialog.Builder(this)
                            .setTitle("Already exists")
                            .setMessage("A project with this name already exists!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        }
        if (requestCode == INTENT_SAVE_SONG) {
            if (resultCode == RESULT_OK) {
                Idea idea = (Idea) data.getSerializableExtra("idea");

                for (int i = 0; i < ideaList.size(); i++) {
                    if (ideaList.get(i).getName().equals(idea.getName())) {
                        ideaList.set(i, idea);
                    }
                }

                ifm.save(ideaList);
            }
        }
    }

    private void launchEditorActivity(Idea i) {
        try {
            Intent editorIntent = new Intent(getApplicationContext(), EditorActivity.class);
            editorIntent.putExtra("idea", i);
            startActivityForResult(editorIntent, INTENT_SAVE_SONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportToExternalStorage(Idea i) {
        if (isStorageWriteable()) {
            String filePath = i.getFileName();

            File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File output = new File(f, filePath.substring(filePath.lastIndexOf('/') + 1));
            File input = new File(filePath);

            try {
                FileOutputStream fos = new FileOutputStream(output);
                FileInputStream fis = new FileInputStream(input);

                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                fis.close();
                fos.close();

                Toast.makeText(this, R.string.save_successful, Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isStorageWriteable() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }
}
