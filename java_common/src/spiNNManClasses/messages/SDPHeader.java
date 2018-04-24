package spiNNManClasses.messages;

import java.nio.ByteBuffer;

public class SDPHeader extends SpiNNakerMessagePart {
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
    public static final int LENGTH = 10;

    private static final byte toByte(int x) {
        return (byte) (x & 0xFF);
    }

    public SDPHeader(int destinationChipX, int destinationChipY,
            int destinationChipP, int destinationPort, int flags, int tag,
            int sourcePort, int sourceCpu, int sourceChipX, int sourceChipY) {
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

    public SDPHeader(ByteBuffer data, int offset) {
        this.flags = data.get(offset + 2);
        this.tag = data.get(offset + 3);
        byte dst = data.get(offset + 4);
        this.destinationPort = (byte) ((dst >> 5) & 7);
        this.destinationChipP = (byte) (dst & 31);
        byte src = data.get(offset + 5);
        this.sourcePort = (byte) ((src >> 5) & 7);
        this.sourceCpu = (byte) (src & 31);
        this.destinationChipY = data.get(offset + 6);
        this.destinationChipX = data.get(offset + 7);
        this.sourceChipY = data.get(offset + 8);
        this.sourceChipX = data.get(offset + 9);
    }

    byte[] convertByteArray() {
        byte[] messageData = new byte[getLength()];

        messageData[0] = 0;
        messageData[1] = 0;
        messageData[2] = getFlags();
        messageData[3] = getTag();

        // Compose Dest_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        messageData[4] = toByte(((getDestinationPort() & 7) << 5)
                | (getDestinationChipP() & 31));

        // Compose Source_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        messageData[5] = toByte(
                ((getSourcePort() & 7) << 5) | (getSourceCpu() & 31));
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
    public final byte getDestinationChipX() {
        return destinationChipX;
    }

    /**
     * @return the destinationChipY
     */
    public final byte getDestinationChipY() {
        return destinationChipY;
    }

    /**
     * @return the destinationChipP
     */
    public final byte getDestinationChipP() {
        return destinationChipP;
    }

    /**
     * @return the destinationPort
     */
    public final byte getDestinationPort() {
        return destinationPort;
    }

    /**
     * @return the flags
     */
    public final byte getFlags() {
        return flags;
    }

    /**
     * @return the tag
     */
    public final byte getTag() {
        return tag;
    }

    /**
     * @param tag
     *            the tag to set
     */
    public void setTag(byte tag) {
        this.tag = tag;
    }

    /**
     * @return the sourcePort
     */
    public final byte getSourcePort() {
        return sourcePort;
    }

    /**
     * @param sourcePort
     *            the sourcePort to set
     */
    public void setSourcePort(byte sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * @return the sourceCpu
     */
    public final byte getSourceCpu() {
        return sourceCpu;
    }

    /**
     * @param sourceCpu
     *            the sourceCpu to set
     */
    public void setSourceCpu(byte sourceCpu) {
        this.sourceCpu = sourceCpu;
    }

    /**
     * @return the sourceChipX
     */
    public final byte getSourceChipX() {
        return sourceChipX;
    }

    /**
     * @param sourceChipX
     *            the sourceChipX to set
     */
    public void setSourceChipX(byte sourceChipX) {
        this.sourceChipX = sourceChipX;
    }

    /**
     * @return the sourceChipY
     */
    public final byte getSourceChipY() {
        return sourceChipY;
    }

    /**
     * @param sourceChipY
     *            the sourceChipY to set
     */
    public void setSourceChipY(byte sourceChipY) {
        this.sourceChipY = sourceChipY;
    }

    /**
     * @return the length
     */
    public final int getLength() {
        return LENGTH;
    }

    @Override
    public void appendTo(ByteBuffer b) {
        b.limit(b.limit() + LENGTH);
        b.put((byte) 0);
        b.put((byte) 0);
        b.put(getFlags());
        b.put(getTag());

        // Compose Dest_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        b.put(toByte(((getDestinationPort() & 7) << 5)
                | (getDestinationChipP() & 31)));

        // Compose Source_port+cpu = 3 MSBs as port and 5 LSBs as cpu
        b.put(toByte(((getSourcePort() & 7) << 5) | (getSourceCpu() & 31)));

        b.put(getDestinationChipY());
        b.put(getDestinationChipX());
        b.put(getSourceChipY());
        b.put(getSourceChipX());
    }
}
