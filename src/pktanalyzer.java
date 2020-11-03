import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class pktanalyzer {

    public static void main(String[] args) throws IOException {
        byte[] inputBytes ;
        if (args.length !=1){
            System.out.println("The program takes exactly 1 Argument and that is the packet in bin format");
            return;
        }

        inputBytes = Files.readAllBytes(Paths.get(args[0]));

        ArrayList<String> enitreFrame = new ArrayList<>();

        // Getting the byte data in String format
        for (byte b : inputBytes){
            enitreFrame.add( ByteToHex.getByteToHexString(b));
        }


        // Initializing the Linked Hashmap to maintain the order of inserts.
        // Overriding it's to string method to print the data in a desired format.
        LinkedHashMap<Object,Object> parsedMap = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+  key + "=" + this.get(key));
                }
                return String.join(" ",printingList);

            }
        };
        // parsing the Ethernet part
        parsedMap.put("Ethernet part of the packet",EthernetParser.parseEthernetList(enitreFrame));

        // parsing the IP part
        ArrayList<Object> ipPart = IpParser.parseIpList(enitreFrame.subList(14,enitreFrame.size()));
        parsedMap.put("Ip part of the Packet",ipPart.get(1));

        // fetching the Protocol residing inside the IP packet and eventually parsing that particular Protocol's packet.
        String nextProtocol =(String)  ((HashMap<Object,Object>) parsedMap.get("Ip part of the Packet")).get("Protocol") ;
        int protocolNumber = Integer.parseInt( nextProtocol.substring(0,nextProtocol.indexOf(" ")));
        if (protocolNumber == 6){
            parsedMap.put("TCP part of the Packet",TCPParser.parseTCPList(enitreFrame.subList(14+(int)ipPart.get(0),enitreFrame.size())));
        }else if (protocolNumber==17){
            parsedMap.put("UDP part of the Packet",UDPParser.parseUDPList(enitreFrame.subList(14+(int)ipPart.get(0),enitreFrame.size())));
        }else if (protocolNumber==1){
            parsedMap.put("ICMP part of the Packet",ICMPParser.parseICMPList(enitreFrame.subList(14+(int)ipPart.get(0),enitreFrame.size())));
        }

        System.out.println(parsedMap);
    }
}
