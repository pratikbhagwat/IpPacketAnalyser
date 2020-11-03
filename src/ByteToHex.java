import java.util.Map;

public class ByteToHex {
    private static Map<String,Character> nibbleToCharMap =  Map.ofEntries(
            Map.entry("0000",'0'),
            Map.entry("0001",'1'),
            Map.entry("0010",'2'),
            Map.entry("0011",'3'),
            Map.entry("0100",'4'),
            Map.entry("0101",'5'),
            Map.entry("0110",'6'),
            Map.entry("0111",'7'),
            Map.entry("1000",'8'),
            Map.entry("1001",'9'),
            Map.entry("1010",'A'),
            Map.entry("1011",'B'),
            Map.entry("1100",'C'),
            Map.entry("1101",'D'),
            Map.entry("1110",'E'),
            Map.entry("1111",'F')
    );

    private static Map<Character,Integer> hexToIntMap =  Map.ofEntries(
            Map.entry('0',0),
            Map.entry('1',1),
            Map.entry('2',2),
            Map.entry('3',3),
            Map.entry('4',4),
            Map.entry('5',5),
            Map.entry('6',6),
            Map.entry('7',7),
            Map.entry('8',8),
            Map.entry('9',9),
            Map.entry('A',10),
            Map.entry('B',11),
            Map.entry('C',12),
            Map.entry('D',13),
            Map.entry('E',14),
            Map.entry('F',15)
    );


    public static String getHexRepresentationOfByteString(String byteString){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(byteString);
        StringBuilder hexString = new StringBuilder();
        while (stringBuilder.length()!=8){
            stringBuilder.insert(0,"0");
        }
        hexString.append( nibbleToCharMap.get(stringBuilder.substring(0,4)));
        hexString.append(nibbleToCharMap.get(stringBuilder.substring(4,8)));
        return hexString.toString();
    }
    public static String getByteToHexString(byte signedByte){
        return getHexRepresentationOfByteString( Integer.toBinaryString (signedByte & 0xFF));
    }
    public static int getHexCharToInt(char c){
        return hexToIntMap.get(c);
    }
    public static int hexStringToInt(String s){
        return Integer.parseInt(s,16);
    }

}
