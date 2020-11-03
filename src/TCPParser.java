import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TCPParser {
    public static Object parseTCPList(List<String> tcpRelatedBytes) {
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> tcpPacket = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "TCP    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };
        // getting the tcp header and the offset in a list format
        ArrayList<Object> nextOffsetAndHeaderPacketTuple = TCPHeaderParser.parseTCPHeader(tcpRelatedBytes);

        tcpPacket.put("TCP Header",nextOffsetAndHeaderPacketTuple.get(1));// adding the tcp header in the packet
        tcpPacket.put("TCP Data", DataParser.parseData(tcpRelatedBytes.subList( (int)nextOffsetAndHeaderPacketTuple.get(0),tcpRelatedBytes.size()))); // adding the tcp data in the packet using the offset from nextOffsetAndHeaderPacketTuple.get(0)

        return tcpPacket;
    }
}
