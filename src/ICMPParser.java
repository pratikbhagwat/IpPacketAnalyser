import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ICMPParser {
    public static Object parseICMPList(List<String> icmpRelatedBytes) {
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> icmpPacket = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "ICMP    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);

            }
        };
        icmpPacket.put("Type" , getType(icmpRelatedBytes.get(0)));

        // based on type adding the information about the code
        if (icmpPacket.get("Type").equals("Destination")){
            icmpPacket.put("Code" , getCode(icmpRelatedBytes.get(1)));
        }else {
            icmpPacket.put("Code", ByteToHex.hexStringToInt(icmpRelatedBytes.get(1)));
        }

        icmpPacket.put("Checksum",getChecksum(icmpRelatedBytes));
        if (icmpRelatedBytes.size() > 4){ // if length is greater than 4 then data is present
            icmpPacket.put("Other Message Specific Information ", String.join(" ", icmpRelatedBytes.subList(4,icmpRelatedBytes.size())));
        }else {
            icmpPacket.put("Other Message Specific Information ","NONE");
        }
        return icmpPacket;
    }

    /**
     *
     * @param icmpRelatedBytes List of ICMP Bytes in Hex Format
     * @return checksum of the ICMP packet
     */
    private static Object getChecksum(List<String> icmpRelatedBytes) {
        StringBuilder checksumInHex = new StringBuilder();
        checksumInHex.append(icmpRelatedBytes.get(2));
        checksumInHex.append(icmpRelatedBytes.get(3));
        return "0x"+checksumInHex.toString();
    }

    /**
     *
     * @param s code in Hex Format
     * @return code and code description (This function is used only when the Type is Destination)
     */
    private static Object getCode(String s) {
        int code = ByteToHex.hexStringToInt(s);
        if (code == 0){
            return code + " Network Unreachable";
        }else  if (code==1){
            return code + " Host Unreachable";
        }else  if (code==3){
            return code + " Port Unreachable";
        }
        return "";
    }

    private static Object getType(String s) {
        int type = ByteToHex.hexStringToInt(s);
        if (type == 0){
            return type + " Echo Reply";
        }else if (type == 3){
            return type + " Destination";
        }else if (type == 8){
            return type + " Echo Request";
        }else if (type == 11){
            return type + " TTL Expired";
        }
        return "";
    }
}
