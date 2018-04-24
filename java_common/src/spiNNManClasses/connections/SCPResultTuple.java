package spiNNManClasses.connections;

import java.nio.ByteBuffer;

import spiNNManClasses.messages.SCPResult;

/**
 * The SCP result code, the sequence number, the data of the response, and the
 * offset at which the data starts (i.e., where the SDP header starts).
 * 
 * @author Donal Fellows
 */
public class SCPResultTuple {
    public final SCPResult result;
    public final int sequence;
    public final ByteBuffer data;
    public final int offset;

    SCPResultTuple(SCPResult result, int sequence, ByteBuffer data, int offset) {
        this.result = result;
        this.sequence = sequence;
        this.data = data;
        this.offset = offset;
    }
}