package spiNNManClasses.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * The SCP Result codes
 * 
 * @author dkf
 */
public enum SCPResult {
    /** SCPCommand completed OK */
    OK(0x80),
    /** Bad packet length */
    LEN(0x81),
    /** Bad checksum */
    SUM(0x82),
    /** Bad/invalid command */
    CMD(0x83),
    /** Invalid arguments */
    ARG(0x84),
    /** Bad port number */
    PORT(0x85),
    /** Timeout */
    TIMEOUT(0x86),
    /** No P2P route */
    ROUTE(0x87),
    /** Bad CPU number */
    CPU(0x88),
    /** SHM destination dead */
    DEAD(0x89),
    /** No free Shared Memory buffers */
    BUF(0x8a),
    /** No reply to open */
    P2P_NOREPLY(0x8b),
    /** Open rejected */
    P2P_REJECT(0x8c),
    /** Destination busy */
    P2P_BUSY(0x8d),
    /** Dest did not respond */
    P2P_TIMEOUT(0x8e),
    /** Pkt Transmission failed */
    PKT_TX(0x8f);
    public final int value;
    private static final Map<Integer, SCPResult> map = new HashMap<>();

    private SCPResult(int value) {
        this.value = value;
    }

    static {
        for (SCPResult r : values())
            map.put(r.value, r);
    }

    public static SCPResult get(int value) {
        SCPResult r = map.get(value);
        if (r == null)
            throw new IllegalArgumentException(
                    "value not mapped in enumeration");
        return r;
    }

}
