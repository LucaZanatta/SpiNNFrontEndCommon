package spiNNManClasses.connections;

import java.nio.ByteBuffer;

import spiNNManClasses.messages.SCPRequest;

/**
 *
 * @author alan
 */
public class SCPRequestPipeLine {
    public SCPRequestPipeLine(UDPConnection connection, int nRetries,
            int timeout, int nChannels, int intermediateChannelWaits) {
        // TODO Auto-generated constructor stub
    }

    public void sendRequest(SCPRequest request, ResultsCallback callback,
            ErrorReceiver error_callback) {
        // TODO Auto-generated method stub

    }

    public void finish() {
        // TODO Auto-generated method stub

    }

    public interface ResultsCallback {
        // TODO contents of this interface
        void receive(ByteBuffer responseData, int responseOffset);
    }

    public interface ErrorReceiver {
        void receiveError(Exception exception);
    }

}
