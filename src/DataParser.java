import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DataParser {
    public static Object parseData(List<String> dataRelatedBytes) {
        LinkedHashMap<Object,Object> tcpData = new LinkedHashMap<>(){
            @Override
            public String toString() {
                ArrayList<String> printingList = new ArrayList<>();

                for(Object key: this.keySet()){
                    printingList.add("\n"+ "Data    " +   key + "=" + this.get(key));
                }
                return String.join(" ",printingList);
            }
        };
        // putting the data related (1st 64) bytes in form of hex string in the packet
        if (dataRelatedBytes.size() >63){
            tcpData.put("Data In Hex Format" , String.join( " ", dataRelatedBytes.subList(0,64)) );
        }else {
            tcpData.put("Data In Hex Format" , String.join( " ", dataRelatedBytes));
        }
        // putting the data related bytes in form of ASCII string in the packet
        tcpData.put("Data In Ascii Format" , getAsciiFormat(dataRelatedBytes));
        return tcpData;
    }

    /**
     *
     * @param dataRelatedBytes List of bytes related to the data in TCP or UDP or any other protocol for that matter.
     * @return first 64 ASCII characters, only if the ascii value is between 32 and 127 inclusive, else it prints '.'
     */
    private static Object getAsciiFormat(List<String> dataRelatedBytes) {
        int count = 0;
        StringBuilder asciiFormat = new StringBuilder();
        for (String hexString : dataRelatedBytes){

            char toBePrinted = (char) (ByteToHex.hexStringToInt(hexString));
            if (toBePrinted < 128 && toBePrinted > 31){
                asciiFormat.append( toBePrinted );
            }else {
                asciiFormat.append( '.' );
            }
            count++;
            if (count == 64){
                break;
            }
        }
        return asciiFormat.toString();
    }
}
