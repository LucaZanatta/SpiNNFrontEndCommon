package spiNNManClasses.connections;

import spiNNManClasses.exceptions.SpinnmanIOException;

public interface SCPReceiver extends Connection {
    /**
     * Determines if there is an SCP packet to be read without blocking
     * 
     * @param timeout
     *            The time to wait before returning if the connection is not
     *            ready
     * @return: True if there is an SCP packet to be read
     */
    boolean isReadyToReceive(int timeout);

    /**
     * Determines if there is an SCP packet to be read without blocking; does
     * not wait
     * 
     * @return: True if there is an SCP packet to be read
     */
    default boolean isReadyToReceive() {
        return isReadyToReceive(0);
    }

    /**
     * Receives an SCP response from this connection. Blocks until a message has
     * been received, or a timeout occurs.
     * 
     * @param timeout
     *            The time in milliseconds to wait for the message to arrive; if
     *            not specified, will wait forever, or until the connection is
     *            closed
     * @return The SCP result, the sequence number, the data of the response and
     *         the offset at which the data starts (i.e. where the SDP header
     *         starts)
     * @exception SpinnmanIOException
     *                If there is an error receiving the message
     * @exception SpinnmanTimeoutException
     *                If there is a timeout before a message is received
     */
    SCPResultTuple receiveSCPResonse(Integer timeout);

    /**
     * Receives an SCP response from this connection. Blocks until a message has
     * been received, or a one-second timeout occurs.
     * 
     * @return The SCP result, the sequence number, the data of the response and
     *         the offset at which the data starts (i.e. where the SDP header
     *         starts)
     * @exception SpinnmanIOException
     *                If there is an error receiving the message
     * @exception SpinnmanTimeoutException
     *                If there is a timeout before a message is received
     */
    default SCPResultTuple receiveSCPResonse() {
        return receiveSCPResonse(1000);
    }

    /**
     * Receives an SCP response from this connection. Blocks until a message has
     * been received, or a timeout occurs.
     * 
     * @param timeout
     *            The time in seconds to wait for the message to arrive.
     * @return The SCP result, the sequence number, the data of the response and
     *         the offset at which the data starts (i.e. where the SDP header
     *         starts)
     * @exception SpinnmanIOException
     *                If there is an error receiving the message
     * @exception SpinnmanTimeoutException
     *                If there is a timeout before a message is received
     */
    default SCPResultTuple receiveSCPResonse(double timeout) {
        return receiveSCPResonse((int) (1000 * timeout));
    }
}
