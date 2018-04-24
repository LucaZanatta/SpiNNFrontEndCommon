package spiNNManClasses.connections;

import java.nio.ByteBuffer;

import spiNNManClasses.messages.SCPResult;
import spiNNManClasses.messages.SDPHeader;

public abstract class SCPResponse {
    protected SDPHeader sdpHeader;
    protected Header scpHeader;

    protected abstract void readDataBytes(ByteBuffer data, int offset);

    /** Reads a packet from a bytestring of data
    
    :param data: The bytestring to be read
    :type data: str
    :param offset: The offset in the data from which the response should\
    be read
    :type offset: int
    */
    public void readByteSequence(ByteBuffer data, int offset) {
        sdpHeader = new SDPHeader(data, offset);
        offset += SDPHeader.LENGTH;
        scpHeader = new Header(data, offset);
        offset += Header.LENGTH;
        readDataBytes(data, offset);
    }

    /** Represents the header of an SCP Response */
    public class Header {
        public static final int LENGTH = 4;
        /** The result of the SCP response */
        public final SCPResult result;
        /** The sequence number of the SCP response, between 0 and 65535 */
        public final Integer sequence;

        public Header() {
            result = null;
            sequence = null;
        }

        public Header(SCPResult result) {
            this.result = result;
            sequence = null;
        }

        public Header(int sequence) {
            result = null;
            this.sequence = sequence;
        }

        public Header(SCPResult result, int sequence) {
            this.result = result;
            this.sequence = sequence;
        }

        /**
         * Read a header from a byte sequence
         * 
         * @param buffer
         *            The byte sequence to read from
         * @param offset
         *            Where to read from
         */
        public Header(ByteBuffer buffer, int offset) {
            result = SCPResult.get(buffer.getShort(offset));
            sequence = (int) buffer.getShort(offset + 2);
        }
    }
}
