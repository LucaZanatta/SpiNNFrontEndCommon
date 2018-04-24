package spiNNManClasses;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static byte SDP_SOURCE_PORT = 7;
    public static byte SDP_SOURCE_CPU = 31;
    public static byte SDP_TAG = (byte) 0xFF;

    /** The default port of the connection */
    public static int SCP_SCAMP_PORT = 17893;

    /** The default port of the connection */
    public static int UDP_BOOT_CONNECTION_DEFAULT_PORT = 54321;

    /** The base address of the system variable structure in System ram */
    public static int SYSTEM_VARIABLE_BASE_ADDRESS = 0xf5007f00;

    /** The base address of a routers diagnostic filter controls */
    public static int ROUTER_REGISTER_BASE_ADDRESS = 0xe1000000;

    /** The base address of a routers p2p routing table */
    public static int ROUTER_REGISTER_P2P_ADDRESS = ROUTER_REGISTER_BASE_ADDRESS
            + 0x10000;

    /** offset for the router filter controls first register (one word each) */
    public static int ROUTER_FILTER_CONTROLS_OFFSET = 0x200;

    /**
     * point where default filters finish and user set-able ones are available
     */
    public static int ROUTER_DEFAULT_FILTERS_MAX_POSITION = 11;

    /** size of a router diagnostic filter control register in bytes */
    public static int ROUTER_DIAGNOSTIC_FILTER_SIZE = 4;

    /** number of router diagnostic filters */
    public static int NO_ROUTER_DIAGNOSTIC_FILTERS = 16;

    /** The size of the system variable structure in bytes */
    public static int SYSTEM_VARIABLE_BYTES = 256;

    /** The max size a UDP packet can be */
    public static int UDP_MESSAGE_MAX_SIZE = 256;

    /** the amount of size in bytes that the EIEIO command header is */
    public static int EIEIO_COMMAND_HEADER_SIZE = 3;

    /** The amount of size in bytes the EIEIO data header is */
    public static int EIEIO_DATA_HEADER_SIZE = 2;

    /** the address of the start of the VCPU structure. (Copied from sark.h) */
    public static int CPU_INFO_OFFSET = 0xe5007000;

    /** how many bytes the cpu info data takes up */
    public static int CPU_INFO_BYTES = 128;

    /** the address at which user0 register starts */
    public static int CPU_USER_0_START_ADDRESS = 112;

    /** the address at which user0 register starts */
    public static int CPU_USER_1_START_ADDRESS = 116;

    /** the address at which user0 register starts */
    public static int CPU_USER_2_START_ADDRESS = 120;

    /** the address at which the iobuf address starts */
    public static int CPU_IOBUF_ADDRESS_OFFSET = 88;

    /** default UDP tag */
    public static int DEFAULT_SDP_TAG = 0xFF;

    /** max user requested tag value */
    public static int MAX_TAG_ID = 7;

    /** The range of values the BMP's 12-bit ADCs can measure. */
    public static int BMP_ADC_MAX = 1 << 12;

    /**
     * Multiplier to convert from ADC value to volts for lines less than 2.5 V.
     */
    public static double BMP_V_SCALE_2_5 = 2.5 / BMP_ADC_MAX;

    /** Multiplier to convert from ADC value to volts for 3.3 V lines. */
    public static double BMP_V_SCALE_3_3 = 3.75 / BMP_ADC_MAX;

    /** Multiplier to convert from ADC value to volts for 12 V lines. */
    public static double BMP_V_SCALE_12 = 15.0 / BMP_ADC_MAX;

    /**
     * Multiplier to convert from temperature probe values to degrees Celsius.
     */
    public static double BMP_TEMP_SCALE = 1.0 / 256.0;

    /** Temperature value returned when a probe is not connected. */
    public static int BMP_MISSING_TEMP = -0x8000;

    /** Fan speed value returned when a fan is absent. */
    public static int BMP_MISSING_FAN = -1;

    /** Timeout for BMP power-on commands to reply. (In seconds) */
    public static double BMP_POWER_ON_TIMEOUT = 10.0;

    /** Timeout for other BMP commands to reply. (In seconds) */
    public static double BMP_TIMEOUT = 0.5;

    /** Time to sleep after powering on boards. (In seconds) */
    public static double BMP_POST_POWER_ON_SLEEP_TIME = 5.0;

    /** number of chips to check are booted fully from the middle */
    public static int NO_MIDDLE_CHIPS_TO_CHECK = 8;

    /** a listing of what spinnaker specific EIEIO commands there are. */
    public enum EIEIOCommandID {
        /** Database handshake with external program */
        DATABASE_CONFIRMATION(1),

        /** Fill in buffer area with padding */
        EVENT_PADDING(2),

        /** End of all buffers, stop execution */
        EVENT_STOP(3),

        /** Stop complaining that there is SDRAM free space for buffers */
        STOP_SENDING_REQUESTS(4),

        /** Start complaining that there is SDRAM free space for buffers */
        START_SENDING_REQUESTS(5),

        /** Spinnaker requesting new buffers for spike source population */
        SPINNAKER_REQUEST_BUFFERS(6),

        /** Buffers being sent from host to SpiNNaker */
        HOST_SEND_SEQUENCED_DATA(7),

        /** Buffers available to be read from a buffered out vertex */
        SPINNAKER_REQUEST_READ_DATA(8),

        /** Host confirming data being read form SpiNNaker memory */
        HOST_DATA_READ(9),

        /**
         * command for notifying the external devices that the simulation has
         * stopped
         */
        STOP_PAUSE_NOTIFICATION(10),

        /**
         * command for notifying the external devices that the simulation has
         * started
         */
        START_RESUME_NOTIFICATION(11),

        /** Host confirming request to read data received */
        HOST_DATA_READ_ACK(12);
        private int value;
        private EIEIOCommandID(int value) {
            this.value = value;
        }
        private static final Map<Integer,EIEIOCommandID> map;
        static {
            map =  new HashMap<>();
            for (EIEIOCommandID e : values())
                map.put(e.value, e);
        }
        public EIEIOCommandID get(int value) {
            EIEIOCommandID e = map.get(value);
            if (e == null)
                throw new IllegalArgumentException("unsupported enumeration value");
            return e;
        }
    }

    /**
     * the values used by the SCP iptag time outs. These control how long to
     * wait for any message request which requires a response, before raising an
     * error. The value is calculated via the following formula:
     * <blockquote>
     * 10ms &times; 2<sup>TagTimeoutValue-1</sup>
     * </blockquote>
     */
    public enum IPTagTimeoutWaitTime {
        TIMEOUT_10ms(1),
        TIMEOUT_20ms(2),
        TIMEOUT_40ms(3),
        TIMEOUT_80ms(4),
        TIMEOUT_160ms(5),
        TIMEOUT_320ms(6),
        TIMEOUT_640ms(7),
        TIMEOUT_1280ms(8),
        TIMEOUT_2560ms(9);
        private int value;
        private IPTagTimeoutWaitTime(int value) {
            this.value = value;
        }
        private static final Map<Integer,IPTagTimeoutWaitTime> map;
        static {
            map =  new HashMap<>();
            for (IPTagTimeoutWaitTime e : values())
                map.put(e.value, e);
        }
        public IPTagTimeoutWaitTime get(int tag_timeout_value) {
            IPTagTimeoutWaitTime e = map.get(tag_timeout_value);
            if (e == null)
                throw new IllegalArgumentException("unsupported enumeration value");
            return e;
        }
    }

    public enum RouterRegisterRegister {
        // Multicast
        LOC_MC(0), EXT_MC(1), DUMP_MC(8),
        // Point-to-point
        LOC_PP(2), EXT_PP(3), DUMP_PP(9),
        // Nearest neighbour
        LOC_NN(4), EXT_NN(5), DUMP_NN(10),
        // Fixed route
        LOC_FR(6), EXT_FR(7), DUMP_FR(11),
        // User registers
        USER_0(12), USER_1(13), USER_2(14), USER_3(15);
        private int value;

        private RouterRegisterRegister(int value) {
            this.value = value;
        }

        private static final Map<Integer, RouterRegisterRegister> map;
        static {
            map = new HashMap<>();
            for (RouterRegisterRegister e : values())
                map.put(e.value, e);
        }

        public RouterRegisterRegister get(int value) {
            RouterRegisterRegister e = map.get(value);
            if (e == null)
                throw new IllegalArgumentException(
                        "unsupported enumeration value");
            return e;
        }
    }

    /**
     * the types of read available from SARK. These values are used to tell SARK
     * how to read the data in a time efficient manner.
     */
    public enum ReadType {
        BYTE(0), HALF_WORD(1), WORD(2);
        public final int value;

        private ReadType(int value) {
            this.value = value;
        }

        private static final Map<Integer, ReadType> map;
        static {
            map = new HashMap<>();
            for (ReadType e : values())
                map.put(e.value, e);
        }

        public ReadType get(int value) {
            ReadType e = map.get(value);
            if (e == null)
                throw new IllegalArgumentException(
                        "unsupported enumeration value");
            return e;
        }
    }

    public enum SDPFlag {
        /** Indicates that a reply is expected */
        REPLY_EXPECTED(0x87),
        /** Indicates that a reply is not expected */
        REPLY_NOT_EXPECTED(0x07);
        public final int value;

        private SDPFlag(int value) {
            this.value = value;
        }

        private static final Map<Integer, SDPFlag> map;
        static {
            map = new HashMap<>();
            for (SDPFlag e : values())
                map.put(e.value, e);
        }

        public SDPFlag get(int value) {
            SDPFlag e = map.get(value);
            if (e == null)
                throw new IllegalArgumentException(
                        "unsupported enumeration value");
            return e;
        }
    }

    /**
     * This is a mapping between read address in the mapping between word byte
     * position, the number of bytes you wish to read, and the type of time
     * efficient way to read said amount of bytes via SARK.
     * <p>
     * Note that the key is an <i>encoding</i> of a pair done by shifting the
     * first value of the pair up by 8 bits and OR-ing with the second value of
     * the pair.
     */
    public static final Map<Integer, ReadType> ADDRESS_LENGTH_DTYPE = new HashMap<>();
    static {
        ADDRESS_LENGTH_DTYPE.put(encode(0, 0), ReadType.WORD);
        ADDRESS_LENGTH_DTYPE.put(encode(0, 1), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(0, 2), ReadType.HALF_WORD);
        ADDRESS_LENGTH_DTYPE.put(encode(0, 3), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(1, 0), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(1, 1), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(1, 2), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(1, 3), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(2, 0), ReadType.HALF_WORD);
        ADDRESS_LENGTH_DTYPE.put(encode(2, 1), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(2, 2), ReadType.HALF_WORD);
        ADDRESS_LENGTH_DTYPE.put(encode(2, 3), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(3, 0), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(3, 1), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(3, 2), ReadType.BYTE);
        ADDRESS_LENGTH_DTYPE.put(encode(3, 3), ReadType.BYTE);
    }

    private static final int encode(int a, int b) {
        return a << 8 | b;
    }

    /** This is the default timeout when using SCP. (In seconds) */
    public static final double SCP_TIMEOUT = 1.0;

}
