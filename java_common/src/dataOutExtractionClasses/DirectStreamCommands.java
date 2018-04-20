package dataOutExtractionClasses;

/**
 * The commands supported by the direct out streaming protocol.
 */
public enum DirectStreamCommands {
    /**
     * Start the streaming. Payload is where to get the data from and how long
     * it is.
     */
    START_SENDING(100),
    /**
     * Request retransmission of packets with the given sequence numbers.
     * Payload begins with the total number of packets describing what is
     * missing, and then is filled out with as many missing sequence numbers as
     * will fit.
     */
    START_MISSING_SEQUENCES(1000),
    /**
     * Provides more sequence numbers that are to be retransmitted. Payload is
     * filled out with as many missing sequence numbers as will fit.
     */
    MORE_MISSING_SEQUENCES(1001);
    private short value;

    DirectStreamCommands(int value) {
        this.value = (short) value;
    }

    /**
     * @return The protocol command code.
     */
    public short getValue() {
        return value;
    }
}
