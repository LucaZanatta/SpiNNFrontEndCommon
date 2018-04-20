package spiNNManClasses.connections;

import spiNNManClasses.messages.SDPHeader;
import spiNNManClasses.Constants;
/**
 *
 * @author alan
 */
public class Utils {

    public static void updateSdpHeaderForUdpSend(
            SDPHeader sdpHeader, int sourceX, int sourceY){
        /** Apply defaults to the sdp header for sending over UDP

        @param sdpHeader: The SDP header values
        @return: Nothing is returned
        */

        sdpHeader.setTag(Constants.SDP_TAG);
        sdpHeader.setSourcePort(Constants.SDP_SOURCE_PORT);
        sdpHeader.setSourceCpu(Constants.SDP_SOURCE_CPU);
        sdpHeader.setSourceChipX((byte) sourceX);
        sdpHeader.setSourceChipY((byte) sourceY);
    }
    
}
