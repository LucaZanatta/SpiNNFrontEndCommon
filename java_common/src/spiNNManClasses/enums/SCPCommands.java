package spiNNManClasses.enums;

/**
 * The commands supported by the SpiNNaker Control Protocol.
 *
 * Or rather a subset of them.
 */
public enum SCPCommands {
    /** Get SCAMP Version */
    VERSION(0),
    /**
     * Start executing code at a specific address.
     * @deprecated Use {@link #APP_START}
     */
    RUN_FROM_ADDRESS(1),
    /** Read SDRAM */
    READ_MEMORY(2),
    /** Write SDRAM */
    WRITE_MEMORY(3),
    /**
     * Execute an already-loaded APLX executable,
     * @deprecated Use {@link #APP_START} 
     */
    RUN_APLX_EXECUTABLE(4),
    /** Fill SDRAM */
    FILL_MEMORY(5),
    /** Application core remap */
    REMAP_CORE(16),
    /** Read neighbouring chip's memory */
    LINK_READ(17),
    /** Write neighbouring chip's memory */
    LINK_WRITE(18),
    /** Application core reset */
    RESET_CORE(19),
    /** Send a Nearest-Neighbour packet */
    SEND_NNP(20),
    /** Point-to-point packet control? */
    P2PC(21),
    /** Send a Signal */
    SEND_SIGNAL(22),
    /** Send Flood-Fill Data */
    FLOOD_FILL(23),
    /** Application core APLX start */
    APP_START(24),
    /** Control the LEDs */
    SET_LED(25),
    /** Set an IPTAG */
    SET_IPTAG(26),
    /** Read/write/erase serial ROM */
    SROM(27),
    /** Allocate or Free SDRAM or Routing entries */
    ALLOCATE(28),
    /** Initialise the router */
    ROUTER(29),
    /** Dropped Packet Reinjection setup */
    DPRI(30),
    /** Get Chip Summary Information */
    GET_INFO(31),
    /** Get BMP info structures */
    BMP_INFO(48),
    BMP_FLASH_COPY(49),
    BMP_FLASH_ERASE(50),
    BMP_FLASH_WRITE(51),
    /**
     * @deprecated What does this do? Nothing?
     */
    BMP_XXX_52(52),
    BMP_SERIAL_FLASH(53),
    BMP_EEPROM(54),
    BMP_RESET(55),
    BMP_XILINX(56),
    /** Turns on or off the machine via BMP */
    BMP_POWER(57),
    BMP_TEST(63),
    TUBE(64);
    private short value;

    SCPCommands(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }
}
