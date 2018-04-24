package spiNNManClasses.exceptions;

/**
 * Encapsulates exceptions from processes which communicate with some core/chip
 */
@SuppressWarnings("serial")
public class SpinnmanGenericProcessException extends RuntimeException {
    public SpinnmanGenericProcessException(int x, int y, int p,
            Throwable cause) {
        super(makeMessage(x, y, p, cause), cause);
    }

    private static String makeMessage(int x, int y, int p, Throwable t) {
        return String.format("\n     Received exception class: %s\n"
                + "     With message: %s\n     When sending to %d:%d:%d",
                t.getClass().getName(), t.getMessage(), x, y, p);
    }
}
