package spiNNManClasses;

import spiNNManClasses.Enums.CPUState;
import spiNNManClasses.Enums.MailBoxCommand;
import spiNNManClasses.Enums.RunTimeError;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class CPUInfo {
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
    
    public CPUInfo( int x, int y, int p, ByteBuffer cpuData, int offset) 
            throws UnsupportedEncodingException{
        /*
        :param x: The x-coordinate of a chip
        :type x: int
        :param y: The y-coordinate of a chip
        :type y: int
        :param p: The id of a core on the chip
        :type p: int
        :param cpu_data: A bytestring received from SDRAM on the board
        :type cpu_data: str
        */
        this.x = x;
        this.y = y;
        this.p = p;
        
        // fill in registers
        for (int index = 0; index < N_REGISTERS; index++){
            this.registers[index] = cpuData.getInt();
        }
        
        this.processorStateRegister = cpuData.getInt();
        this.stackPointer = cpuData.getInt();
        this.linkRegister = cpuData.getInt();
        this.runTimeError = RunTimeError.valueOf(cpuData.get());
        this.physicalCpuId = cpuData.get();
        this.state = CPUState.valueOf(cpuData.get());
        this.applicationId = cpuData.get();
        this.applicationMailBoxDataAddress = cpuData.getInt();
        this.monitorMailboxDataAddress = cpuData.getInt();
        this.applicationMailboxCommand = MailBoxCommand.valueOf(cpuData.get());
        this.monitorMailboxCommand = MailBoxCommand.valueOf(cpuData.get());
        this.softwareErrorCount = cpuData.getShort();
        this.softwareSourceFilenameAddress = cpuData.getInt();
        this.softwareSourceLineNumber = cpuData.getInt();
        this.time = cpuData.getInt();
        
        byte [] applicationNameRaw = new byte[APPLICATION_NAME_BYTES];
        for (int index = 0; index < APPLICATION_NAME_BYTES; index++){
            applicationNameRaw[index] = cpuData.get();
        }
        this.applicationName = new String(applicationNameRaw, "UTF-8");
        
        this.iobufAddress = cpuData.getInt();
        this.softwareVersion = cpuData.getInt();
        
        for (int index = 0; index < PADDING_BYTES; index++){
            cpuData.get();
        }
        
        this.user[0] = cpuData.getInt();
        this.user[1] = cpuData.getInt();
        this.user[2] = cpuData.getInt();
        this.user[3] = cpuData.getInt();
     
        int index = this.applicationName.indexOf('\0');
        if (index != -1){
            this.applicationName = this.applicationName.substring(0, index);
        }
        
    }
    
    @Override
    public String toString(){
        return "" + this.getX() + ":" + this.getY() + ":" + this.getP() + " " + 
            this.getState().getName() + " " + this.getApplicationName() +
            " " +  this.getApplicationId();
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the p
     */
    public int getP() {
        return p;
    }

    /**
     * @return the registers
     */
    public int[] getRegisters() {
        return registers;
    }

    /**
     * @return the processorStateRegister
     */
    public int getProcessorStateRegister() {
        return processorStateRegister;
    }

    /**
     * @return the stackPointer
     */
    public int getStackPointer() {
        return stackPointer;
    }

    /**
     * @return the linkRegister
     */
    public int getLinkRegister() {
        return linkRegister;
    }

    /**
     * @return the runTimeError
     */
    public RunTimeError getRunTimeError() {
        return runTimeError;
    }

    /**
     * @return the physicalCpuId
     */
    public byte getPhysicalCpuId() {
        return physicalCpuId;
    }

    /**
     * @return the state
     */
    public CPUState getState() {
        return state;
    }

    /**
     * @return the applicationId
     */
    public byte getApplicationId() {
        return applicationId;
    }

    /**
     * @return the applicationMailBoxDataAddress
     */
    public int getApplicationMailBoxDataAddress() {
        return applicationMailBoxDataAddress;
    }

    /**
     * @return the monitorMailboxDataAddress
     */
    public int getMonitorMailboxDataAddress() {
        return monitorMailboxDataAddress;
    }

    /**
     * @return the applicationMailboxCommand
     */
    public MailBoxCommand getApplicationMailboxCommand() {
        return applicationMailboxCommand;
    }

    /**
     * @return the monitorMailboxCommand
     */
    public MailBoxCommand getMonitorMailboxCommand() {
        return monitorMailboxCommand;
    }

    /**
     * @return the softwareErrorCount
     */
    public short getSoftwareErrorCount() {
        return softwareErrorCount;
    }

    /**
     * @return the softwareSourceFilenameAddress
     */
    public int getSoftwareSourceFilenameAddress() {
        return softwareSourceFilenameAddress;
    }

    /**
     * @return the softwareSourceLineNumber
     */
    public int getSoftwareSourceLineNumber() {
        return softwareSourceLineNumber;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @return the iobufAddress
     */
    public int getIobufAddress() {
        return iobufAddress;
    }

    /**
     * @return the softwareVersion
     */
    public int getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * @return the user
     */
    public int[] getUser() {
        return user;
    }
}