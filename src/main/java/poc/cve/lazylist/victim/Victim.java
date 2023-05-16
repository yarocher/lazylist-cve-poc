package poc.cve.lazylist.victim;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static poc.cve.lazylist.util.SerdeUtil.*;

/**
 * Victim class which reads and deserializes payload data from given file argument (or stdout if "-").
 * WARNING: if testing FILE_OUT function0 provider, be careful to not truncate your own files by accident if
 * running this class on your local machine.
 */
public class Victim {
    public static void main(String[] args) throws IOException {
        String deserializationSource = args[0];
        try (InputStream is = "-".equals(deserializationSource) ? System.in : new FileInputStream(deserializationSource)) {
            byte[] data = is.readAllBytes();
            deserialize(data);
        }
    }
}
