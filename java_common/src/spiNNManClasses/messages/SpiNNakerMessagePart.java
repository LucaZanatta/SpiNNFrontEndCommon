package spiNNManClasses.messages;

import static java.lang.System.arraycopy;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

public abstract class SpiNNakerMessagePart {
    public static final int DEFAULT_MESSAGE_CAPACITY = 320;

    /**
     * Convert this message to a sequence of bytes.
     * 
     * @return The bytes
     */
    public final byte[] getBytestring() {
        return getBytestring(DEFAULT_MESSAGE_CAPACITY);
    }

    /**
     * Convert this message to a sequence of bytes.
     * 
     * @param messageCapacity
     *            How much space to allocate for intermediate working buffers.
     * @return The bytes
     */
    public final byte[] getBytestring(int messageCapacity) {
        ByteBuffer b = allocate(messageCapacity);
        b.order(LITTLE_ENDIAN);
        b.rewind();
        b.limit(0);
        appendTo(b);
        byte[] result = new byte[b.limit()];
        arraycopy(b.array(), 0, result, 0, b.limit());
        return result;
    }

    /**
     * Add the message contents to the given buffer. The buffer can be assumed
     * to be little endian and to have the limit set to point to where the
     * insertion should happen; <i>extending the limit is expected</i>.
     * 
     * @param b
     */
    public abstract void appendTo(ByteBuffer b);
}
