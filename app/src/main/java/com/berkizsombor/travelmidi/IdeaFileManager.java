package com.berkizsombor.travelmidi;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by berki on 2017. 02. 14..
 */

public class IdeaFileManager {

    private Context context;

    private String dataFileName;

    public IdeaFileManager(Context context) {
        this.context = context;
        this.dataFileName = context.getString(R.string.data_filename);
    }

    public void save(List<Idea> ideas) {
        File f = new File(context.getFilesDir(), dataFileName);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ideas);
            fos.close();

            if (!f.exists()) {
                f.mkdirs();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Idea> load() {
        List<Idea> ideas = new ArrayList<Idea>();
        File f = new File(context.getFilesDir(), dataFileName);
        try {

            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            ideas = (List<Idea>) ois.readObject();
            fis.close();

            return ideas;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            return ideas;
        }
    }
}
