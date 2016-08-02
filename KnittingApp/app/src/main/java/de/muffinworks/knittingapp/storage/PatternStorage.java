package de.muffinworks.knittingapp.storage;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.storage.models.Metadata;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternStorage {

    private static final String TAG = "PatternStorage";

    private Context mContext;
    private Gson mGson = new Gson();
    private HashMap<String, Metadata> mMetaDataTable;

    private static PatternStorage storage = new PatternStorage();

    public static PatternStorage getInstance() {
        if (storage != null) {
            return storage;
        } else {
            return new PatternStorage();
        }
    }

    private PatternStorage() {
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        loadMetadata();
    }

    private String getApplicationDir() {
        return mContext.getFilesDir().getPath();
    }

    private String getFilePathInApplicationDir(String fileName) {
        return getApplicationDir() + "/" + fileName;
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
                Environment.getExternalStorageDirectory().canWrite();
    }

    public void exportAll() throws IOException {
        for (String id : mMetaDataTable.keySet()) {
            export(id);
        }
    }

    public File export(String id) throws IOException {
        if(!isExternalStorageWritable())
            throw new IOException("External storage is not mounted!");
        File patternFile = new File(getFilePathInApplicationDir(id + ".json"));
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Knitting Patterns");
        file.mkdirs();
        file = new File(file, id + ".json");
        copyFile(patternFile, file);
        return file;
    }

    public void importPattern(String path) throws IOException {
        save(loadFromFile(path));
    }



    /**
     * Copied from https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
     * @param src
     * @param dst
     * @throws IOException
     */
    private void copyFile(File src, File dst) throws IOException {
        FileOutputStream outStream = new FileOutputStream(dst);
        FileInputStream inStream = new FileInputStream(src);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    private void loadMetadata() {
        mMetaDataTable = new HashMap<>();

        try {
            File file = new File(getApplicationDir(), Constants.METADATA_FILENAME);
            FileReader fileReader = new FileReader(file);
            //https://sites.google.com/site/gson/gson-user-guide#TOC-Collections-Examples
            Type metadataLisType = new TypeToken<List<Metadata>>(){}.getType();
            List<Metadata> metadata = mGson.fromJson(fileReader, metadataLisType);

            try {
                fileReader.close();
            } catch (IOException e) {
                logError(mContext.getString(R.string.error_load_metadata));
                e.printStackTrace();
            }

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
            logError(mContext.getString(R.string.error_update_metadata));
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
            logError(mContext.getString(R.string.error_save_pattern));
            e.printStackTrace();
        }
    }

    public Pattern loadFromFile(String path) {
        try {
            FileReader reader = new FileReader(path);
            return mGson.fromJson(reader, Pattern.class);
        } catch (FileNotFoundException e) {
            logError(mContext.getString(R.string.error_file_not_found, path));
            return null;
        }
    }

    public boolean checkPatternDuplicate(Pattern pattern) {
        String id = pattern.getId();
        return mMetaDataTable.containsKey(id);
    }

    public Pattern load(String id){
        return loadFromFile(getFilePathInApplicationDir(id + ".json"));
    }

    public void clearAll() {
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

    private void logError(String message) {
        Log.e(TAG, message);
    }
}
