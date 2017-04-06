package com.berkizsombor.travelmidi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zsombor on 2017. 01. 30..
 */

public class IdeaAdapter extends ArrayAdapter<Idea> {
    private final Context context;

    private List<Idea> ideas;
    private List<Idea> ideasUnfiltered;

    private IdeaFileManager ifm;

    private View row;

    public IdeaAdapter(Context context, List<Idea> ideas, IdeaFileManager ifm) {
        super(context, R.layout.idea_list_item, ideas);
        this.context = context;
        this.ideas = ideas;
        this.ideasUnfiltered = ideas;
        this.ifm = ifm;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        row = convertView;

        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.idea_list_item, parent, false);
        }

        TextView name = (TextView) row.findViewById(R.id.name);
        TextView tags = (TextView) row.findViewById(R.id.tags);
        final ImageButton delete = (ImageButton) row.findViewById(R.id.delete);

        name.setText(ideas.get(position).getName());
        tags.setText(getHashtags(ideas.get(position)));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertDialog(position);
            }
        });

        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {

        Filter ideaFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults fr = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // no filter
                    fr.values = ideasUnfiltered;
                    fr.count = ideasUnfiltered.size();
                } else {
                    List<Idea> filteredIdeas = new ArrayList<Idea>();

                    for (Idea i : ideasUnfiltered) {
                        for (String tag : i.getTags()) {
                            if (tag.contains(constraint)) {
                                filteredIdeas.add(i);
                            }
                        }
                    }

                    fr.values = filteredIdeas;
                    fr.count = filteredIdeas.size();
                }

                return fr;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    ideas = (List<Idea>) results.values;
                    notifyDataSetChanged();
                }
            }
        };

        return ideaFilter;
    }

    @Override
    public int getCount() {
        return ideas != null ? ideas.size() : 0;
    }

    private String getHashtags(Idea i) {

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < i.getTags().size(); j++) {
            sb.append("#").append(i.getTags().get(j));

            if (j != i.getTags().size() - 1)
                sb.append(", ");
        }

        return sb.toString();
    }

    private void deleteAlertDialog(final int pos) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());

        adb.setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete_message);

        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete MIDI data
                File midi = new File(ideas.get(pos).getFileName());

                if (midi.exists()) midi.delete();

                // delete project
                ideas.remove(pos);
                ifm.save(ideas);

                notifyDataSetChanged();
            }
        });

        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing I guess?
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }
}
