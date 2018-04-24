package spiNNManClasses.processes.abstractClasses;

import java.util.HashMap;

import spiNNManClasses.Constants;
import spiNNManClasses.connections.SCPRequestPipeLine;
import spiNNManClasses.connections.SCPRequestPipeLine.ErrorReceiver;
import spiNNManClasses.connections.SCPRequestPipeLine.ResultsCallback;
import spiNNManClasses.connections.UDPConnection;
import spiNNManClasses.messages.SCPRequest;
import spiNNManClasses.processes.connectionSelectors.AbstractMultiConnectionProcessConnectionSelector;

/**
 * A process that uses multiple connections in communication.
 * 
 * @author alan
 * @author Donal
 */
public abstract class AbstractMultiConnectionProcess extends AbstractProcess {
    protected HashMap<UDPConnection, SCPRequestPipeLine> scpRequestPipelines = new HashMap<>();
    protected static final int DEFAULT_RETRIES = 3;
    protected int nRetries = 3;
    protected static final double DEFAULT_TIMEOUT = Constants.SCP_TIMEOUT;
    protected int timeout;
    protected static final int DEFAULT_INTERMEDIATE_WAITS = 7;
    protected int intermediateChannelWaits;
    protected static final int DEFAULT_CHANNELS = 8;
    protected int nChannels;
    protected AbstractMultiConnectionProcessConnectionSelector nextConnectionSelector;

    protected AbstractMultiConnectionProcess(
            AbstractMultiConnectionProcessConnectionSelector next_connection_selector,
            int n_retries, double timeout, int n_channels,
            int intermediate_channel_waits) {
        this.timeout = (int) (timeout * 1000);
        this.nChannels = n_channels;
        this.nRetries = n_retries;
        this.intermediateChannelWaits = intermediate_channel_waits;
        this.nextConnectionSelector = next_connection_selector;
    }

    protected AbstractMultiConnectionProcess(
            AbstractMultiConnectionProcessConnectionSelector next_connection_selector) {
        this(next_connection_selector, DEFAULT_RETRIES, DEFAULT_TIMEOUT,
                DEFAULT_CHANNELS, DEFAULT_INTERMEDIATE_WAITS);
    }

    protected void sendRequest(final SCPRequest request,
            ResultsCallback callback, ErrorReceiver error_callback) {
        if (error_callback == null) {
            error_callback = (b) -> defaultReceiveError(request, b);
        }
        UDPConnection connection = nextConnectionSelector
                .getNextConnection(request);
        if (!scpRequestPipelines.containsKey(connection)) {
            scpRequestPipelines.put(connection, new SCPRequestPipeLine(
                    connection, nRetries, timeout, nChannels,
                    intermediateChannelWaits));
        }
        scpRequestPipelines.get(connection).sendRequest(request, callback,
                error_callback);
    }

    protected void sendRequest(SCPRequest request, ResultsCallback callback) {
        sendRequest(request, callback, null);
    }

    protected void sendRequest(SCPRequest request) {
        sendRequest(request, null, null);
    }

    protected void finish() {
        for (SCPRequestPipeLine request_pipeline : scpRequestPipelines.values())
            request_pipeline.finish();
    }
}
