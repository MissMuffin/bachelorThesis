package de.muffinworks.knittingapp.services;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import de.muffinworks.knittingapp.services.models.Metadata;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 23.07.2016.
 */
@RunWith(AndroidJUnit4.class)
public class PatternStorageServiceTest {

    private Context context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    private PatternStorageService service;

    @Before
    public void setup() throws IOException {
        service = PatternStorageService.getInstance();
        service.init(context);
        service.clearAll();
    }

    @Test
    public void saveAndLoadTest() throws IOException {
        assertNull(service.load("fileIdThatDoesNotExist"));

        Pattern pattern = new Pattern();
        pattern.setName("mulatto");
        pattern.setColumns(12);
        pattern.setRows(5);
        pattern.setCurrentRow(2);
        pattern.setPatternRows(new String[5]);
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

        PatternStorageService service2 = PatternStorageService.getInstance();
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
