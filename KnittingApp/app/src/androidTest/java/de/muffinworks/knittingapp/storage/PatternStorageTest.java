package de.muffinworks.knittingapp.storage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import de.muffinworks.knittingapp.storage.models.Metadata;
import de.muffinworks.knittingapp.storage.models.Pattern;

/**
 * Created by Bianca on 23.07.2016.
 */
@RunWith(AndroidJUnit4.class)
public class PatternStorageTest {

    private Context context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    private PatternStorage storage;

    @Before
    public void setup() throws IOException {
        storage = PatternStorage.getInstance();
        storage.init(context);
        storage.clearAll();
    }

    @Test
    public void saveAndLoadTest() throws IOException {
        assertNull(storage.load("fileIdThatDoesNotExist"));

        Pattern pattern = new Pattern();
        pattern.setName("mulatto");
        pattern.setCurrentRow(2);
        pattern.setPatternRows(new String[]{
                "3e",
                "2er",
                "ttt",
                "3f",
                "3t"
        });
        assertEquals(3, pattern.getColumns());
        assertEquals(5, pattern.getRows());

        storage.save(pattern);

        File test = new File(context.getFilesDir().getPath().toString()+ "/" + pattern.getFilename());
        assertTrue(test.exists());

        Pattern p2 = storage.load(pattern.getId());
        assertEquals(pattern.equals(p2), true);
    }

    @Test
    public void listEntriesTest() throws IOException {
        assertTrue(storage.listMetadataEntries().length == 0);
        for(int i = 1; i <= 5; i++) {
            Pattern pattern = new Pattern();
            storage.save(pattern);
            assertTrue(storage.listMetadataEntries().length == i);
        }
    }

    @Test
    public void deleteTest() throws IOException {
        saveAndLoadTest();

        PatternStorage storage2 = PatternStorage.getInstance();
        assertTrue(storage2.listMetadataEntries().length > 0);

        String id = storage2.listMetadataEntries()[0].getId();
        storage.delete(id);
        for (Metadata m : storage2.listMetadataEntries()) {
            if(m.getId().equals(id))
                fail();
        }
    }

    @After
    public void cleanUp() throws IOException {
        storage.clearAll();
    }
}
