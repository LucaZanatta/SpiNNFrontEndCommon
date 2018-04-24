package spiNNManClasses.model;

import spiNNManClasses.model.enums.CPUState;
import spiNNManClasses.model.enums.MailBoxCommand;
import spiNNManClasses.model.enums.RunTimeError;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import commonClasses.HasCoreLocation;

public class CPUInfo implements HasCoreLocation {
    private static final int PADDING_BYTES = 16;
    private static final int N_REGISTERS = 8;
    private static final int N_USER_REGISTERS = 4;
    private static final int APPLICATION_NAME_BYTES = 16;

    private final int x;
    private final int y;
    private final int p;
    private final int[] registers = new int[N_REGISTERS];
    private final int processorStateRegister;
    private final int stackPointer;
    private final int linkRegister;
    private final RunTimeError runTimeError;
    private final byte physicalCpuId;
    private final CPUState state;
    private final byte applicationId;
    private final int applicationMailBoxDataAddress;
    private final int monitorMailboxDataAddress;
    private final MailBoxCommand applicationMailboxCommand;
    private final MailBoxCommand monitorMailboxCommand;
    private final short softwareErrorCount;
    private final int softwareSourceFilenameAddress;
    private final int softwareSourceLineNumber;
    private final int time;
    private String applicationName;
    private final int iobufAddress;
    private final int softwareVersion;
    private final int[] user = new int[N_USER_REGISTERS];

    /**
     * 
     * @param x
     *            The x-coordinate of a chip
     * @param y
     *            The y-coordinate of a chip
     * @param p
     *            The id of a core on the chip
     * @param cpuData
     *            Bytes received from SDRAM on the board
     * @param offset
     *            Where in the byte buffer to start parsing from
     * @throws UnsupportedEncodingException
     */
    public CPUInfo(int x, int y, int p, ByteBuffer cpuData, Integer offset)
            throws UnsupportedEncodingException {
        this.x = x;
        this.y = y;
        this.p = p;

        if (offset != null)
            cpuData.position(offset);

        // fill in registers
        for (int index = 0; index < N_REGISTERS; index++) {
            registers[index] = cpuData.getInt();
        }

        processorStateRegister = cpuData.getInt();
        stackPointer = cpuData.getInt();
        linkRegister = cpuData.getInt();
        runTimeError = RunTimeError.valueOf(cpuData.get());
        physicalCpuId = cpuData.get();
        state = CPUState.valueOf(cpuData.get());
        applicationId = cpuData.get();
        applicationMailBoxDataAddress = cpuData.getInt();
        monitorMailboxDataAddress = cpuData.getInt();
        applicationMailboxCommand = MailBoxCommand.valueOf(cpuData.get());
        monitorMailboxCommand = MailBoxCommand.valueOf(cpuData.get());
        softwareErrorCount = cpuData.getShort();
        softwareSourceFilenameAddress = cpuData.getInt();
        softwareSourceLineNumber = cpuData.getInt();
        time = cpuData.getInt();

        byte[] applicationNameRaw = new byte[APPLICATION_NAME_BYTES];
        for (int index = 0; index < APPLICATION_NAME_BYTES; index++) {
            applicationNameRaw[index] = cpuData.get();
        }
        applicationName = new String(applicationNameRaw, "UTF-8");

        iobufAddress = cpuData.getInt();
        softwareVersion = cpuData.getInt();

        for (int index = 0; index < PADDING_BYTES; index++) {
            cpuData.get();
        }

        user[0] = cpuData.getInt();
        user[1] = cpuData.getInt();
        user[2] = cpuData.getInt();
        user[3] = cpuData.getInt();

        int index = applicationName.indexOf('\0');
        if (index != -1) {
            applicationName = applicationName.substring(0, index);
        }
    }

    private static final String OUTPUT_TEMPLATE = "%d:%d:%d %s %s %d";

    @Override
    public String toString() {
        return String.format(OUTPUT_TEMPLATE, getX(), getY(), getP(),
                getState().getName(), getApplicationName(), getApplicationId());
    }

    /**
     * @return the x coordinate of the chip
     */
    @Override
    public final int getX() {
        return x;
    }

    /**
     * @return the y coordinate of the chip
     */
    @Override
    public final int getY() {
        return y;
    }

    /**
     * @return the processor ID on the chip
     */
    @Override
    public final int getP() {
        return p;
    }

    /**
     * @return the registers
     */
    public final int[] getRegisters() {
        return registers;
    }

    /**
     * @return the processor state register
     */
    public final int getProcessorStateRegister() {
        return processorStateRegister;
    }

    /**
     * @return the stack pointer
     */
    public final int getStackPointer() {
        return stackPointer;
    }

    /**
     * @return the link register
     */
    public final int getLinkRegister() {
        return linkRegister;
    }

    /**
     * @return the runtime error
     */
    public final RunTimeError getRunTimeError() {
        return runTimeError;
    }

    /**
     * @return the physical cpu ID
     */
    public final byte getPhysicalCpuId() {
        return physicalCpuId;
    }

    /**
     * @return the state
     */
    public final CPUState getState() {
        return state;
    }

    /**
     * @return the application ID
     */
    public final byte getApplicationId() {
        return applicationId;
    }

    /**
     * @return the application mailbox data address
     */
    public final int getApplicationMailBoxDataAddress() {
        return applicationMailBoxDataAddress;
    }

    /**
     * @return the monitor mailbox data address
     */
    public final int getMonitorMailboxDataAddress() {
        return monitorMailboxDataAddress;
    }

    /**
     * @return the current application mailbox command
     */
    public final MailBoxCommand getApplicationMailboxCommand() {
        return applicationMailboxCommand;
    }

    /**
     * @return the current monitor mailbox command
     */
    public final MailBoxCommand getMonitorMailboxCommand() {
        return monitorMailboxCommand;
    }

    /**
     * @return the software error count
     */
    public final short getSoftwareErrorCount() {
        return softwareErrorCount;
    }

    /**
     * @return the softwareSourceFilenameAddress
     */
    public final int getSoftwareSourceFilenameAddress() {
        return softwareSourceFilenameAddress;
    }

    /**
     * @return the software source line number
     */
    public final int getSoftwareSourceLineNumber() {
        return softwareSourceLineNumber;
    }

    /**
     * @return the time
     */
    public final int getTime() {
        return time;
    }

    /**
     * @return the application name
     */
    public final String getApplicationName() {
        return applicationName;
    }

    /**
     * @return the iobuf address
     */
    public final int getIobufAddress() {
        return iobufAddress;
    }

    /**
     * @return the software version
     */
    public final int getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * @return the user registers
     */
    public final int[] getUser() {
        return user;
    }
}
