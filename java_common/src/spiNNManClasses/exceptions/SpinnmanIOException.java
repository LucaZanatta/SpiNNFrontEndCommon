package spiNNManClasses.exceptions;

@SuppressWarnings("serial")
public class SpinnmanIOException extends RuntimeException {
    public SpinnmanIOException(String message, Throwable cause) {
        super(message, cause);
    }
    public SpinnmanIOException(String message) {
        this(message, null);
    }
    public SpinnmanIOException(Throwable cause) {
        this("problem talking to SpiNNaker board", cause);
    }
    public SpinnmanIOException() {
        this("problem talking to SpiNNaker board", null);
    }
}
