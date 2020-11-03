import java.util.*;

public class EthernetParser {

    public static Map<Object,Object> parseEthernetList(List<String> ethernetRelatedBytes ){
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> ethernetMap = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n" +"Ethernet    "+ key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };

        // getting size of the entire frame
        ethernetMap.put("PacketSize",ethernetRelatedBytes.size());
        // getting Destination Address
        ethernetMap.put("Destination Address",String.join(":",ethernetRelatedBytes.subList(0,6)));
        // getting Source Address
        ethernetMap.put("Source Address",String.join(":",ethernetRelatedBytes.subList(6,12)));


        // getting type
        StringBuilder typeBuilder = new StringBuilder();
        for (int i = 12;i<14;i++){
            typeBuilder.append(ethernetRelatedBytes.get(i));
        }
        ethernetMap.put("type",typeBuilder.toString() + " type IP");
        return ethernetMap;
    }
}
