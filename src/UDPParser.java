import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UDPParser {
    public static Object parseUDPList(List<String> udpRelatedBytes) {
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> udpPacket = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "UDP    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };
        // getting the udp header and the offset in a list format
        udpPacket.put("UDP Header",UDPHeaderParser.parseUDPHeader(udpRelatedBytes));// adding the udp header in the packet
        udpPacket.put("UDP Data", DataParser.parseData(udpRelatedBytes.subList(8,udpRelatedBytes.size()))); // as the size of the UDP header is fixed we know the offset for the data bytes.

        return udpPacket;
    }
}
