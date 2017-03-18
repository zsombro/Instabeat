package com.berkizsombor.travelmidi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewIdeaActivity extends AppCompatActivity {


    Button saveButton, cancelButton;
    EditText nameText, tagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        saveButton = (Button) findViewById(R.id.save);
        cancelButton = (Button) findViewById(R.id.cancel);

        nameText = (EditText) findViewById(R.id.name);
        tagText = (EditText) findViewById(R.id.tags);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("name", nameText.getText().toString());
                result.putExtra("tags", getTagsFromString(tagText.getText().toString()));
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private String[] getTagsFromString(String tags) {
        return tags.replace(" ", "").split(",");
    }
}
