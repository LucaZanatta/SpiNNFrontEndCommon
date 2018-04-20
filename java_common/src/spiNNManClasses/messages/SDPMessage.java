package spiNNManClasses.messages;

import static java.lang.System.arraycopy;

public class SDPMessage {
    public static final int MAX_PACKET_SIZE = 300;
    public static final int MAX_PACKET_SIZE_DATA = 292;
    public static final int REPLY_NOT_EXPECTED = 0x07;
    public static final int REPLY_EXPECTED = 0x87;

    private final byte[] data;
    private final SDPHeader header;

    public SDPMessage(
            int destinationChipX, int destinationChipY,
            int destinationChipP, int destinationPort, int flags, int tag,
            int sourcePort, int sourceCpu, int sourceChipX,
            int sourceChipY, byte[] data) {
        this.data = data;
        header = new SDPHeader(
            destinationChipX, destinationChipY, destinationChipP, 
            destinationPort, flags, tag, sourcePort, sourceCpu, 
            sourceChipX, sourceChipY);
    }

    public byte[] convertToByteArray() {
        byte[] messageData = new byte[header.length() + data.length];
        arraycopy(header.convertByteArray(), 0, messageData, 0,
                  header.length());
        arraycopy(data, 0, messageData, header.length(), data.length);
        return messageData;
    }

    public int lengthInBytes() {
        return data.length + header.length();
    }
    
    public SDPHeader getSDPHeader(){
        return this.header;
    }
}
