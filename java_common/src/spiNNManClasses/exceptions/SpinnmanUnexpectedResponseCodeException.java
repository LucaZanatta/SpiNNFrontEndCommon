package spiNNManClasses.exceptions;

import spiNNManClasses.messages.SCPResult;

/**
 * Indicate that a response code returned from the board was unexpected for the
 * current operation
 */
@SuppressWarnings("serial")
public class SpinnmanUnexpectedResponseCodeException extends RuntimeException {
    /** The operation being performed */
    public final String operation;
    /** The command being executed */
    public final String command;
    /** The unexpected response */
    public final SCPResult response;

    /**
     * @param operation
     *            The operation being performed
     * @param command
     *            The command being executed
     * @param response
     *            The response received in error
     */
    public SpinnmanUnexpectedResponseCodeException(String operation,
            String command, SCPResult response) {
        super(String.format(
                "Unexpected response %s while performing operation %s using command %s",
                response, operation, command));
        this.operation = operation;
        this.command = command;
        this.response = response;
    }
}
