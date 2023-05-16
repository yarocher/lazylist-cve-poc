package poc.cve.lazylist.payload;

public interface PayloadGenerator {
    /**
     * Takes optional Object arguments and generates java deserialization payload
     * with implementation-specific gadget chain.
     * @param args - options arguments array, depends on implementation
     * @return - byte array with serialized data (payload)
     */
    byte[] generatePayload(Object... args);
}
