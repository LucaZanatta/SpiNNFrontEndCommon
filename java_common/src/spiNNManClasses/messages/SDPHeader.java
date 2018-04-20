package spiNNManClasses.messages;

public class SDPHeader {
    private final byte destinationChipX;
    private final byte destinationChipY;
    private final byte destinationChipP;
    private final byte destinationPort;
    private final byte flags;
    private byte tag;
    private byte sourcePort;
    private byte sourceCpu;
    private byte sourceChipX;
    private byte sourceChipY;
    private final int length = 10;

    private static final byte toByte(int x) {
        return (byte) (x & 0xFF);
    }

    public SDPHeader(
            int destinationChipX, int destinationChipY,
            int destinationChipP, int destinationPort, int flags, int tag,
            int sourcePort, int sourceCpu, int sourceChipX,
            int sourceChipY) {
        this.destinationChipX = toByte(destinationChipX);
        this.destinationChipY = toByte(destinationChipY);
        this.destinationChipP = toByte(destinationChipP);
        this.destinationPort = toByte(destinationPort);
        this.flags = toByte(flags);
        this.tag = toByte(tag);
        this.sourcePort = toByte(sourcePort);
        this.sourceCpu = toByte(sourceCpu);
        this.sourceChipX = toByte(sourceChipX);
        this.sourceChipY = toByte(sourceChipY);
    }

    byte[] convertByteArray() {
        byte[] messageData = new byte[getLength()];

        messageData[0] = 0;
        messageData[1] = 0;
        messageData[2] = getFlags();
        messageData[3] = getTag();

        // Compose Dest_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        messageData[4] = 
            toByte(((getDestinationPort() & 7) << 5) | 
                    (getDestinationChipP() & 31));

        // Compose Source_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        messageData[5] = 
            toByte(((getSourcePort() & 7) << 5) | (getSourceCpu() & 31));
        messageData[6] = getDestinationChipY();
        messageData[7] = getDestinationChipX();
        messageData[8] = getSourceChipY();
        messageData[9] = getSourceChipX();

        return messageData;
    }

    int length() {
        return getLength();
    }

    /**
     * @return the destinationChipX
     */
    public byte getDestinationChipX() {
        return destinationChipX;
    }

    /**
     * @return the destinationChipY
     */
    public byte getDestinationChipY() {
        return destinationChipY;
    }

    /**
     * @return the destinationChipP
     */
    public byte getDestinationChipP() {
        return destinationChipP;
    }

    /**
     * @return the destinationPort
     */
    public byte getDestinationPort() {
        return destinationPort;
    }

    /**
     * @return the flags
     */
    public byte getFlags() {
        return flags;
    }

    /**
     * @return the tag
     */
    public byte getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(byte tag) {
        this.tag = tag;
    }

    /**
     * @return the sourcePort
     */
    public byte getSourcePort() {
        return sourcePort;
    }

    /**
     * @param sourcePort the sourcePort to set
     */
    public void setSourcePort(byte sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * @return the sourceCpu
     */
    public byte getSourceCpu() {
        return sourceCpu;
    }

    /**
     * @param sourceCpu the sourceCpu to set
     */
    public void setSourceCpu(byte sourceCpu) {
        this.sourceCpu = sourceCpu;
    }

    /**
     * @return the sourceChipX
     */
    public byte getSourceChipX() {
        return sourceChipX;
    }

    /**
     * @param sourceChipX the sourceChipX to set
     */
    public void setSourceChipX(byte sourceChipX) {
        this.sourceChipX = sourceChipX;
    }

    /**
     * @return the sourceChipY
     */
    public byte getSourceChipY() {
        return sourceChipY;
    }

    /**
     * @param sourceChipY the sourceChipY to set
     */
    public void setSourceChipY(byte sourceChipY) {
        this.sourceChipY = sourceChipY;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }
}
