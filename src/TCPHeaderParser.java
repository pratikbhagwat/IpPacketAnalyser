import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TCPHeaderParser {
    public static ArrayList<Object> parseTCPHeader(List<String> tcpRelatedBytes) {
        // Initializing arraylist to store the data offsset and the header packet.
        ArrayList<Object> nextOffsetAndHeaderPacketTuple = new ArrayList<>();
        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> tcpHeader = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "TCP    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };
        tcpHeader.put("Source Port",getSourcePort(tcpRelatedBytes));
        tcpHeader.put("Destination Port",getDestinationPort(tcpRelatedBytes));
        tcpHeader.put("Sequence Number",getSequenceNumber(tcpRelatedBytes));
        tcpHeader.put("Acknowledgement Number",getAcknowledgementNumber(tcpRelatedBytes));
        tcpHeader.put("Data Offset",getDataOffset(tcpRelatedBytes));
        tcpHeader.put("Flags",getFlags(tcpRelatedBytes));
        dealWithFlags( ((String) tcpHeader.get("Flags")).substring(((String) tcpHeader.get("Flags")).length()-2) , tcpHeader);
        tcpHeader.put("Window",getWindowNumber(tcpRelatedBytes));
        tcpHeader.put("Checksum",getChecksum(tcpRelatedBytes));
        tcpHeader.put("Urgent Pointer",getUrgentPointer(tcpRelatedBytes));
        int tcpHeaderLength = (int)  tcpHeader.get("Data Offset")  ;
        if ( tcpHeaderLength > 20){// if length greater than 20 options are available
            tcpHeader.put("Options","Available");
        }else {
            tcpHeader.put("Options","Not Available");
        }
        // storing the data offset and the header.
        nextOffsetAndHeaderPacketTuple.add(tcpHeaderLength);
        nextOffsetAndHeaderPacketTuple.add(tcpHeader);
        return nextOffsetAndHeaderPacketTuple;
    }

    private static void dealWithFlags(String flags, LinkedHashMap<Object, Object> tcpHeader) {
        StringBuilder binaryStringBuilderForFlags = new StringBuilder( Integer.toBinaryString(ByteToHex.hexStringToInt(flags)) );

        // getting in 6 bit format
        while (binaryStringBuilderForFlags.length()!=6){
            binaryStringBuilderForFlags.insert(0,"0");
        }

        getUrgentPointerFlag(binaryStringBuilderForFlags.charAt(0) , tcpHeader);
        getAcknowledgementFlag(binaryStringBuilderForFlags.charAt(1) , tcpHeader);
        getPushFlag(binaryStringBuilderForFlags.charAt(2) , tcpHeader);
        getResetFlag(binaryStringBuilderForFlags.charAt(3) , tcpHeader);
        getSynFlag(binaryStringBuilderForFlags.charAt(4) , tcpHeader);
        getFinFlag(binaryStringBuilderForFlags.charAt(5) , tcpHeader);
    }

    /**
     *
     * @param charAt bit responsible for the Urgent Fin Flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getFinFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put(".... ...1","Fin");
        }else {
            tcpHeader.put(".... ...0","No Fin");
        }
    }

    /**
     *
     * @param charAt bit responsible for the Urgent Syn Flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getSynFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put(".... ..1.","Syn");
        }else {
            tcpHeader.put(".... ..0.","No Syn");
        }
    }

    /**
     *
     * @param charAt bit responsible for the Urgent Reset Flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getResetFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put(".... .1..","Reset");
        }else {
            tcpHeader.put(".... .0..","No Reset");
        }
    }

    /**
     *
     * @param charAt bit responsible for the Urgent Push Flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getPushFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put(".... 1...","Push");
        }else {
            tcpHeader.put(".... 0...","No Push");
        }
    }
    /**
     *
     * @param charAt bit responsible for the Urgent Acknowledgement Flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getAcknowledgementFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put("...1 ....","Acknowledgement");
        }else {
            tcpHeader.put("...0 ....","No Acknowledgement");
        }
    }

    /**
     *
     * @param charAt bit responsible for the Urgent Pointer flag
     * @param tcpHeader TCP header Packet i.e. linked HashMap
     */
    private static void getUrgentPointerFlag(char charAt, LinkedHashMap<Object, Object> tcpHeader) {
        if (charAt =='1'){
            tcpHeader.put("..1. ....","Urgent Pointer Significant");
        }else {
            tcpHeader.put("..0. ....","No Urgent Pointer");
        }
    }


    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return byte related to flag in hex format
     */
    private static Object getFlags(List<String> tcpRelatedBytes) {
        int flagInt = ByteToHex.hexStringToInt( tcpRelatedBytes.get(13) ) & 0b00111111;
        return "0x"+ Integer.toHexString(flagInt);
    }

    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return data offset
     */
    private static Object getDataOffset(List<String> tcpRelatedBytes) {
        return ByteToHex.getHexCharToInt( tcpRelatedBytes.get(12).charAt(0) ) * 4 ;

    }
    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return Urgent number
     */
    private static Object getUrgentPointer(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(18));
        stringBuilder.append(tcpRelatedBytes.get(19));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return Checksum number
     */
    private static Object getChecksum(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(16));
        stringBuilder.append(tcpRelatedBytes.get(17));
        return "0x" + stringBuilder.toString();
    }
    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return Window number
     */
    private static Object getWindowNumber(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(14));
        stringBuilder.append(tcpRelatedBytes.get(15));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return Acknowledgement number
     */
    private static Object getAcknowledgementNumber(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(8));
        stringBuilder.append(tcpRelatedBytes.get(9));
        stringBuilder.append(tcpRelatedBytes.get(10));
        stringBuilder.append(tcpRelatedBytes.get(11));
        return Long.parseLong(stringBuilder.toString(),16);
    }

    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return sequence number
     */
    private static Object getSequenceNumber(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(4));
        stringBuilder.append(tcpRelatedBytes.get(5));
        stringBuilder.append(tcpRelatedBytes.get(6));
        stringBuilder.append(tcpRelatedBytes.get(7));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return destination port
     */
    private static Object getDestinationPort(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(2));
        stringBuilder.append(tcpRelatedBytes.get(3));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }

    /**
     *
     * @param tcpRelatedBytes List of Bytes related to TCP packet
     * @return source port
     */
    private static Object getSourcePort(List<String> tcpRelatedBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tcpRelatedBytes.get(0));
        stringBuilder.append(tcpRelatedBytes.get(1));
        return ByteToHex.hexStringToInt(stringBuilder.toString());
    }
}
