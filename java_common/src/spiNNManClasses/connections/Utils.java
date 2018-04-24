package spiNNManClasses.connections;

import static spiNNManClasses.Constants.SDP_SOURCE_CPU;
import static spiNNManClasses.Constants.SDP_SOURCE_PORT;
import static spiNNManClasses.Constants.SDP_TAG;

import spiNNManClasses.messages.SDPHeader;

/**
 *
 * @author alan
 */
public class Utils {

    /**
     * Apply defaults to the SDP header for sending over UDP.
     * 
     * @param sdpHeader
     *            The SDP header values
     */
    public static void updateSdpHeaderForUdpSend(SDPHeader sdpHeader,
            int sourceX, int sourceY) {
        sdpHeader.setTag(SDP_TAG);
        sdpHeader.setSourcePort(SDP_SOURCE_PORT);
        sdpHeader.setSourceCpu(SDP_SOURCE_CPU);
        sdpHeader.setSourceChipX((byte) sourceX);
        sdpHeader.setSourceChipY((byte) sourceY);
    }

}
