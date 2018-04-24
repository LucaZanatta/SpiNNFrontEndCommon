package spiNNManClasses.processes.abstractClasses;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

import spiNNManClasses.exceptions.SpinnmanGenericProcessException;
import spiNNManClasses.messages.SCPRequest;
import spiNNManClasses.messages.SDPHeader;

/**
 * An abstract process for talking to SpiNNaker efficiently.
 * 
 * @author alan
 */
public abstract class AbstractProcess {
    private Logger log = getLogger(AbstractProcess.class.getName());
    protected Exception exception;
    protected SCPRequest errorRequest;

    protected void defaultReceiveError(SCPRequest request,
            Exception exception) {
        this.exception = exception;
        this.errorRequest = request;
    }

    public final boolean isError() {
        return exception != null;
    }

    public void checkForError() {
        checkForError(false);
    }

    public void checkForError(boolean printException) {
        if (exception != null) {
            SDPHeader sdp_header = errorRequest.getSdpHeader();
            if (printException) {
                log.log(Level.WARNING,
                        String.format("failure in request to (%d, %d, %d)",
                                sdp_header.getDestinationChipX(),
                                sdp_header.getDestinationChipY(),
                                sdp_header.getDestinationChipP()),
                        exception);
            }
            SpinnmanGenericProcessException ex = new SpinnmanGenericProcessException(
                    sdp_header.getDestinationChipX(),
                    sdp_header.getDestinationChipY(),
                    sdp_header.getDestinationChipP(), exception);
            exception = ex;
            throw ex;
        }
    }
}
