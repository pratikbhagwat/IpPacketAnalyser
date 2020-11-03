import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UDPHeaderParser {
    public static LinkedHashMap<Object,Object> parseUDPHeader(List<String> udpRelatedBytes) {
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> udpHeader = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "UDP    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };
        udpHeader.put("Source Port",getSourcePort(udpRelatedBytes));
        udpHeader.put("Destination Port",getDestinationPort(udpRelatedBytes));
        udpHeader.put("Length",getLength(udpRelatedBytes));
        udpHeader.put("Checksum",getChecksum(udpRelatedBytes));
        return udpHeader;
    }
    /**
     *
     * @param udpRelatedBytes List of Bytes related to UDP packet
     * @return checksum
     */
    private static Object getChecksum(List<String> udpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(udpRelatedBytes.get(6));
        stringBuilder.append(udpRelatedBytes.get(7));
        return "0x"+ stringBuilder.toString();
    }
    /**
     *
     * @param udpRelatedBytes List of Bytes related to UDP packet
     * @return packet length
     */
    private static Object getLength(List<String> udpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(udpRelatedBytes.get(4));
        stringBuilder.append(udpRelatedBytes.get(5));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
    /**
     *
     * @param udpRelatedBytes List of Bytes related to UDP packet
     * @return destination port
     */
    private static Object getDestinationPort(List<String> udpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(udpRelatedBytes.get(2));
        stringBuilder.append(udpRelatedBytes.get(3));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }

    /**
     *
     * @param udpRelatedBytes List of Bytes related to UDP packet
     * @return source port
     */
    private static Object getSourcePort(List<String> udpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(udpRelatedBytes.get(0));
        stringBuilder.append(udpRelatedBytes.get(1));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
}
