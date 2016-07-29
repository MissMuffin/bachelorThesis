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
    private PatternStorage service;

    @Before
    public void setup() throws IOException {
        service = PatternStorage.getInstance();
        service.init(context);
        service.clearAll();
    }

    @Test
    public void saveAndLoadTest() throws IOException {
        assertNull(service.load("fileIdThatDoesNotExist"));

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

        service.save(pattern);

        File test = new File(context.getFilesDir().getPath().toString()+ "/" + pattern.getFilename());
        assertTrue(test.exists());

        Pattern p2 = service.load(pattern.getId());
        assertEquals(pattern.equals(p2), true);
    }

    @Test
    public void listEntriesTest() throws IOException {
        assertTrue(service.listMetadataEntries().length == 0);
        for(int i = 1; i <= 5; i++) {
            Pattern pattern = new Pattern();
            service.save(pattern);
            assertTrue(service.listMetadataEntries().length == i);
        }
    }

    @Test
    public void deleteTest() throws IOException {
        saveAndLoadTest();

        PatternStorage service2 = PatternStorage.getInstance();
        assertTrue(service2.listMetadataEntries().length > 0);

        String id = service2.listMetadataEntries()[0].getId();
        service.delete(id);
        for (Metadata m : service2.listMetadataEntries()) {
            if(m.getId().equals(id))
                fail();
        }
    }

    @After
    public void cleanUp() throws IOException {
        service.clearAll();
    }
}
