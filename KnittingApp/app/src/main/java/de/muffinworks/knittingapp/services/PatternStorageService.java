package de.muffinworks.knittingapp.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import de.muffinworks.knittingapp.services.models.Metadata;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternStorageService {

    private Context mContext;
    private Gson mGson = new Gson();
    private HashMap<String, Metadata> mMetaDataTable;

    private static PatternStorageService service = new PatternStorageService();

    public static PatternStorageService getInstance() {
        if (service != null) {
            return service;
        } else {
            return new PatternStorageService();
        }
    }

    private PatternStorageService() {
    }

    public void init(Context context) {
        try {
            this.mContext = context.getApplicationContext();
            loadMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getApplicationDir() {
        return mContext.getFilesDir().getPath();
    }

    private String getFilePathInApplicationDir(String fileName) {
        return getApplicationDir() + "/" + fileName;
    }

    private void loadMetadata() throws IOException {
        mMetaDataTable = new HashMap<>();

        try {
            File file = new File(getApplicationDir(), Constants.METADATA_FILENAME);
            FileReader fileReader = new FileReader(file);
            //https://sites.google.com/site/gson/gson-user-guide#TOC-Collections-Examples
            Type metadataLisType = new TypeToken<List<Metadata>>(){}.getType();
            List<Metadata> metadata = mGson.fromJson(fileReader, metadataLisType);
            fileReader.close();

            if (metadata != null) {
                for (Metadata m : metadata) {
                    mMetaDataTable.put(m.getId(), m);
                }
            }
        } catch (FileNotFoundException e) {
            mMetaDataTable = new HashMap<>();
            return;
        }
    }

    private void updateMetadata() {
        try {
            File file = new File(getApplicationDir(), Constants.METADATA_FILENAME);
            String json = mGson.toJson(mMetaDataTable.values());
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            // TODO: 24.07.2016 handle
            e.printStackTrace();
        }
    }

    public Metadata[] listMetadataEntries() {
        Metadata[] m = mMetaDataTable.values().toArray(new Metadata[mMetaDataTable.size()]);
        if (m.length > 0) {
            Arrays.sort(m);
        }
        return m;
    }

    public void save(Pattern pattern) {
        try {
            FileWriter fileWriter = new FileWriter(getFilePathInApplicationDir(pattern.getFilename()));
            fileWriter.write(mGson.toJson(pattern));
            fileWriter.close();
            //call clone to put only metadata information into hashmap, not actual pattern related
            //information -> needs less resources
            mMetaDataTable.put(pattern.getId(), pattern.clone());
            updateMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pattern load(String id){
        try {
            FileReader reader = new FileReader(getFilePathInApplicationDir(id + ".json"));
            return mGson.fromJson(reader, Pattern.class);
        } catch (FileNotFoundException e) {
            return  null;
        }
    }

    public void clearAll() throws IOException {
        for (Metadata m : mMetaDataTable.values()) {
            getFileFromApplicationDir(m.getFilename()).delete();
        }
        getFileFromApplicationDir(Constants.METADATA_FILENAME).delete();
        mMetaDataTable.clear();
    }

    public void delete(Metadata pattern) {
        getFileFromApplicationDir(pattern.getFilename()).delete();
        mMetaDataTable.remove(pattern.getId());
        updateMetadata();
    }

    public void delete(String id) {
        delete(mMetaDataTable.get(id));
    }

    private File getFileFromApplicationDir(String filename) {
        return new File(getApplicationDir(), filename);
    }
}
