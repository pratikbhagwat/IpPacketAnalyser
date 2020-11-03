import java.util.*;

public class IpParser {

    public static ArrayList<Object> parseIpList(List<String> ipRelatedBytes ){
        //storing the IP packet and the data offset So that our next procedure can know where the data is located.
        ArrayList<Object> startOfNextPacketAndThisPacket = new ArrayList<>();

        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> ipPacket = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n" +"IP    "+ key + "=" + this.get(key));
                }
                return String.join(" ",printingList);

            }
        };
        ipPacket.put("Version",getIpVersion(ipRelatedBytes.get(0)));
        ipPacket.put("Header Length",getHeaderLength(ipRelatedBytes.get(0))*4 + " Bytes" );
        dealWithTypeOfSService(ipPacket,ipRelatedBytes.get(1));
        ipPacket.put("Total Length",getTotalLength(ipRelatedBytes) + " Bytes");
        ipPacket.put("Identification",getIdentification(ipRelatedBytes));
        // putting the nibble related to the flags
        ipPacket.put("Flags","0x"+ipRelatedBytes.get(6).charAt(0));
        dealWithFlags(ipPacket,ipRelatedBytes);
        dealWithOffset(ipPacket,ipRelatedBytes);
        ipPacket.put("Time To Live",getTimeToLive(ipRelatedBytes) + " seconds/Hops");
        ipPacket.put("Protocol",getProtocol(ipRelatedBytes));
        ipPacket.put("Header Checksum",getHeaderChecksum(ipRelatedBytes));
        ipPacket.put("Source Address" , getSourceAddress(ipRelatedBytes.subList(12,16)));
        ipPacket.put("Destination Address" , getDestinationAddress(ipRelatedBytes.subList(16,20)));
        int headerLength =  Integer.parseInt(((String) ipPacket.get("Header Length")).substring(0,((String) ipPacket.get("Header Length")).indexOf(" ")));

        if (headerLength > 20){
            ipPacket.put("Options" , "Available");
        }else {
            ipPacket.put("Options" , "Not Available");
        }


        startOfNextPacketAndThisPacket.add( headerLength);
        startOfNextPacketAndThisPacket.add(ipPacket);


        return startOfNextPacketAndThisPacket;
    }


    /**
     *
     * @param hexDestinationAddressList : List containing the destination address i.e 4 bytes
     * @return destination IP address
     */
    private static Object getDestinationAddress(List<String> hexDestinationAddressList) {
        List<String> destinationIpAddressInList = new ArrayList<>();
        for (String listItem:hexDestinationAddressList){
            destinationIpAddressInList.add(""+ByteToHex.hexStringToInt(listItem));
        }
        return String.join(".",destinationIpAddressInList);
    }

    /**
     *
     * @param hexSourceAddressList : List containing the source address i.e 4 bytes
     * @return source IP address
     */
    private static Object getSourceAddress(List<String> hexSourceAddressList) {
        List<String> sourceIpAddressInList = new ArrayList<>();
        for (String listItem:hexSourceAddressList){
            sourceIpAddressInList.add(""+ByteToHex.hexStringToInt(listItem));
        }
        return String.join(".",sourceIpAddressInList);

    }

    /**
     * description: puts the information of offset in the packet.
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param ipRelatedBytes ipRelatedBytes bytes in the IP packet(in hex format)
     */
    private static void dealWithOffset(LinkedHashMap<Object, Object> ipPacket, List<String> ipRelatedBytes) {
        String offsetRelatedFirstByte = ipRelatedBytes.get(6);
        int offset = ByteToHex.hexStringToInt( Integer.toHexString(ByteToHex.hexStringToInt(offsetRelatedFirstByte) & 0x1F) + ipRelatedBytes.get(7) );
        ipPacket.put("Fragment Offset" ,offset );
    }

    /**
     * description: puts the information of flags in the packet.
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param ipRelatedBytes ipRelatedBytes bytes in the IP packet(in hex format)
     */
    private static void dealWithFlags(LinkedHashMap<Object, Object> ipPacket, List<String> ipRelatedBytes) {
        String flagRelatedByte = ipRelatedBytes.get(6);
        StringBuilder stringBuilder = new StringBuilder( Integer.toBinaryString( ByteToHex.hexStringToInt(flagRelatedByte) ) );
        while (stringBuilder.length()!=8){
            stringBuilder.insert(0,"0");
        }
        dealWithDontFragment(ipPacket,stringBuilder.charAt(1));
        dealWithMoreFragment(ipPacket,stringBuilder.charAt(2));
    }

    /**
     * Description: Puts the More fragment information in the packet
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param charAt : Bit responsible for More Fragmentation
     */
    private static void dealWithMoreFragment(LinkedHashMap<Object, Object> ipPacket, char charAt) {
        if (charAt == '1'){
            ipPacket.put("..1. ....","More Fragment");
        }else{
            ipPacket.put("..0. ....","Last Fragment");
        }
    }

    /**
     * Description: Puts the Dont fragment information in the packet
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param charAt : Bit responsible for Fragmentation
     */
    private static void dealWithDontFragment(LinkedHashMap<Object, Object> ipPacket, char charAt) {
        if (charAt == '1'){
            ipPacket.put(".1.. ....","Don't Fragment");
        }else{
            ipPacket.put(".0.. ....","Ok to Fragment");
        }
    }

    /**
     *
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param tosByte: HexString where TOS is stored
     */
    private static void dealWithTypeOfSService(LinkedHashMap<Object, Object> ipPacket, String tosByte) {
        int result = ByteToHex.hexStringToInt(tosByte) & 0xE0;
        StringBuilder resultString = new StringBuilder(Integer.toBinaryString(result));
        while (resultString.length()!=8){
            resultString.insert(0,"0");
        }
        System.out.println(resultString);
        ipPacket.put("DSCP","0x"+tosByte);
        putPrecedence(ipPacket,resultString.toString().substring(0,3));
        putDelay(ipPacket,resultString.charAt(3));
        putThroughput(ipPacket,resultString.charAt(3));
        putReliablity(ipPacket,resultString.charAt(3));
    }

    /**
     *
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param charAt : Delay bit in form of char
     */
    private static void putDelay(LinkedHashMap<Object, Object> ipPacket, char charAt) {
        if (charAt == '1'){
            ipPacket.put("...1 ....","Low Delay");
        }else {
            ipPacket.put("...0 ....","Normal Delay");
        }
    }

    /**
     *
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param charAt : Throughput bit in form of char
     */
    private static void putThroughput(LinkedHashMap<Object, Object> ipPacket, char charAt) {
        if (charAt == '1'){
            ipPacket.put(".... 1...","High Throughput");
        }else {
            ipPacket.put(".... 0...","Normal Throughput");
        }
    }

    /**
     *
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param charAt : Reliablity bit in form of char
     */
    private static void putReliablity(LinkedHashMap<Object, Object> ipPacket, char charAt) {
        if (charAt == '1'){
            ipPacket.put(".... .1..","High Reliability");
        }else {
            ipPacket.put(".... .0..","Normal Reliability");
        }
    }

    /**
     *
     * @param ipPacket Entire ip packet generated so far i.e LinkedHashmap
     * @param : Bit Sequence of Precedence
     */
    private static void putPrecedence(LinkedHashMap<Object, Object> ipPacket, String substring) {
        /**
         *           111 - Network Control
         *           110 - Internetwork Control
         *           101 - CRITIC/ECP
         *           100 - Flash Override
         *           011 - Flash
         *           010 - Immediate
         *           001 - Priority
         *           000 - Routine
         */
        String value = "";
        if (substring.equals("111")){
            value = "Network Control";
        }else if (substring.equals("110")){
            value = "Internetwork Control";
        }
        else if(substring.equals("101")){
            value = "CRITIC/ECP";
        }
        else if(substring.equals("100")){
            value = "Flash Override";
        }
        else if(substring.equals("011")){
            value = "Flash";
        }
        else if(substring.equals("010")){
            value = "Immediate";
        }
        else if(substring.equals("001")){
            value = "Priority";
        }
        else if(substring.equals("000")){
            value = "Routine";
        }
        ipPacket.put(substring+".....",value);
    }

    /**
     *
     * @param ipRelatedBytes ipRelatedBytes bytes in the IP packet(in hex format)
     * @return header checksum
     */
    private static Object getHeaderChecksum(List<String> ipRelatedBytes) {
        StringBuilder headerChecksumStringBuilder = new StringBuilder();
        headerChecksumStringBuilder.append(ipRelatedBytes.get(10));
        headerChecksumStringBuilder.append(ipRelatedBytes.get(11));
        return "0x" +headerChecksumStringBuilder.toString();
    }

    /**
     *
     * @param ipRelatedBytes ipRelatedBytes bytes in the IP packet(in hex format)
     * @return protocol of the packet inside this IP packet
     */
    private static Object getProtocol(List<String> ipRelatedBytes) {
        int protocol = ByteToHex.hexStringToInt(ipRelatedBytes.get(9));
        if (protocol == 1){
            return protocol + " (ICMP)";
        }else if (protocol ==6){
            return protocol + " (TCP)";
        }else if (protocol==17){
            return protocol + " (UDP)";
        }
        return protocol + " (Some Unknown Protocol)";
    }

    /**
     *
     * @param ipRelatedBytes bytes in the IP packet(in hex format)
     * @return Time to live
     */
    private static Object getTimeToLive(List<String> ipRelatedBytes) {
        return ByteToHex.hexStringToInt(ipRelatedBytes.get(8));

    }

    /**
     *
     * @param ipRelatedBytes bytes in the IP packet(in hex format)
     * @return identification number
     */
    private static int getIdentification(List<String> ipRelatedBytes) {
        StringBuilder identificationStringBuilder = new StringBuilder();
        identificationStringBuilder.append(ipRelatedBytes.get(4));
        identificationStringBuilder.append(ipRelatedBytes.get(5));
        return ByteToHex.hexStringToInt(identificationStringBuilder.toString());
    }

    /**
     *
     * @param s:hexstring in which Header Length resides
     * @return header length of the packet(in form of number of Words)
     */
    private static int getHeaderLength(String s) {
        return ByteToHex.getHexCharToInt( s.charAt(1) );
    }

    /**
     *
     * @param s : hexstring in which version resides
     * @return version of IP
     */
    private static int getIpVersion(String s) {
        return ByteToHex.getHexCharToInt( s.charAt(0) );
    }

    /**
     *
     * @param ipRelatedBytes bytes in the IP packet(in hex format)
     * @return total length of packet
     */
    private static int getTotalLength(List<String> ipRelatedBytes){
        StringBuilder totalLengthStringBuilder = new StringBuilder();
        totalLengthStringBuilder.append(ipRelatedBytes.get(2));
        totalLengthStringBuilder.append(ipRelatedBytes.get(3));
        return ByteToHex.hexStringToInt(totalLengthStringBuilder.toString());
    }
}
