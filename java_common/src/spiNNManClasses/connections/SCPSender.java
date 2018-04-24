package spiNNManClasses.connections;

import java.nio.ByteBuffer;

import spiNNManClasses.exceptions.SpinnmanIOException;
import spiNNManClasses.messages.SCPRequest;

/**
 * A sender of SCP messages
 * 
 * @author dkf
 *
 */
public interface SCPSender extends Connection {
    /**
     * Returns the data of an SCP request as it would be sent down this\
     * connection
     */
    ByteBuffer getSCPData(SCPRequest scp_request);

    /**
     * Sends an SCP request down this connection.
     * <p>
     * Messages must have the following properties:
     * <ul>
     * <li>source_port is None or 7
     * <li>source_cpu is None or 31
     * <li>source_chip_x is None or 0
     * <li>source_chip_y is None or 0
     * </ul>
     * tag in the message is optional; if not set the default set in the
     * constructor will be used.
     * <p>
     * sequence in the message is optional; if not set (sequence number last
     * assigned + 1) % 65536 will be used
     * 
     * @param scp_request
     *            message packet to send
     * @exception SpinnmanIOException
     *                If there is an error sending the message
     */
    void sendSCPRequest(SCPRequest scp_request) throws SpinnmanIOException;

    /**
     * The x-coordinate of the chip at which messages sent down this connection
     * will arrive at first
     */
    int chipX();

    /**
     * The y-coordinate of the chip at which messages sent down this connection
     * will arrive at first
     */
    int chipY();
}
