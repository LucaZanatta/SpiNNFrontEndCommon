package spiNNManClasses.messages;

import java.nio.ByteBuffer;

import spiNNManClasses.connections.SCPResponse;
import spiNNManClasses.model.enums.SCPCommands;

/**
 * Represents an Abstract SCP Request
 */
public abstract class SCPRequest extends SpiNNakerMessagePart {
    public static final int DEFAULT_DEST_X_COORD = 255;
    public static final int DEFAULT_DEST_Y_COORD = 255;
    private Integer argument_1, argument_2, argument_3;
    private byte[] data;
    private SDPHeader sdpHeader;
    private SCPRequest.Header scpHeader;

    /**
     * 
     * @param sdp_header:
     *            The SDP header of the request
     * @param scp_request_header:
     *            The SCP header of the request :type scp_request_header:\
     *            :py:class:`spinnman.messages.scp.scp_request_header.SCPRequestHeader`
     * @param argument_1
     *            The first argument, or null if no first argument
     * @param argument_2
     *            The second argument, or null if no second argument
     * @param argument_3
     *            The third argument, or null if no third argument
     * @param data
     *            The optional data, or null if no data
     */
    protected SCPRequest(SDPHeader sdp_header,
            SCPRequest.Header scp_request_header, Integer argument_1,
            Integer argument_2, Integer argument_3, byte[] data) {
        sdpHeader = sdp_header;
        scpHeader = scp_request_header;
        this.argument_1 = argument_1;
        this.argument_2 = argument_2;
        this.argument_3 = argument_3;
        this.data = data;
    }

    /** The SDP header of the message */
    public final SDPHeader getSdpHeader() {
        return sdpHeader;
    }

    /** The SCP request header of the message */
    public final SCPRequest.Header getScpRequestHeader() {
        return scpHeader;
    }

    /** The first argument, or null if no first argument */
    public Integer getArgument1() {
        return argument_1;
    }

    /** The second argument, or null if no second argument */
    public Integer getArgument2() {
        return argument_2;
    }

    /** The third argument, or null if no third argument */
    public Integer getArgument3() {
        return argument_3;
    }

    /** The data, or null if no data */
    public byte[] getData() {
        return data;
    }

    /**
     * The request as a bytestring
     * 
     * @return The request as bytes
     */
    @Override
    public void appendTo(ByteBuffer b) {
        sdpHeader.appendTo(b);
        scpHeader.appendTo(b);
        b.limit(b.limit() + 12);
        b.putInt(argument_1 == null ? 0 : argument_1);
        b.putInt(argument_2 == null ? 0 : argument_2);
        b.putInt(argument_3 == null ? 0 : argument_3);
        if (data != null) {
            b.limit(b.limit() + data.length);
            b.put(data);
        }
    }

    /**
     * Get an SCP response message to be used to process any response received
     * 
     * @return An SCP response, or null if no response is required
     */
    public abstract SCPResponse getScpResponse();

    /**
     * Represents the header of an SCP Request. Each optional parameter in the
     * constructor can be set to a value other than None once, after which it is
     * immutable. It is an error to set a parameter that is not currently None.
     */
    public static class Header extends SpiNNakerMessagePart {
        /** The command of the SCP packet */
        public final SCPCommands command;
        /** The sequence number of the SCP packet */
        public int sequence;

        public Header(SCPCommands command) {
            this(command, 0);
        }

        /**
         * 
         * @param command
         *            The SCP command
         * @param sequence
         *            The number of the SCP packet in order of all packets sent
         *            or received, between 0 and 65535
         */
        public Header(SCPCommands command, int sequence) {
            this.command = command;
            this.sequence = sequence;
        }

        /** Add the bytes for this header to the buffer. */
        @Override
        public void appendTo(ByteBuffer b) {
            b.limit(b.limit() + 4);
            b.putShort(command.getValue());
            b.putShort((short) (sequence & 0xFFFF));
        }
    }
}
