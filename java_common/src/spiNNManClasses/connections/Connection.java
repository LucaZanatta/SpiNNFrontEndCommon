package spiNNManClasses.connections;

import spiNNManClasses.exceptions.SpinnmanIOException;

/**
 * An abstract connection to the SpiNNaker board over some medium.
 */
public interface Connection extends AutoCloseable {
    /**
     * Determines if the medium is connected at this point in time.
     * 
     * @return True if the medium is connected, False otherwise
     * @throws SpinnmanIOException
     *             If there is an error when determining the connectivity of the
     *             medium.
     */
    boolean isConnected() throws SpinnmanIOException;
}
