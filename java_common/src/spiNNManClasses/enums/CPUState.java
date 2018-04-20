package spiNNManClasses.enums;

import java.util.HashMap;

public enum CPUState {
    DEAD(0),
    POWERED_DOWN(1),
    RUN_TIME_EXCEPTION(2),
    WATCHDOG(3),
    INITIALISING(4),
    READY(5),
    C_MAIN(6),
    RUNNING(7),
    SYNC0(8),
    SYNC1(9),
    PAUSED(10),
    FINISHED(11),
    CPU_STATE_12(12),
    CPU_STATE_13(13),
    CPU_STATE_14(14),
    IDLE(15);

    private static final HashMap<Integer, CPUState> map = new HashMap<>();
    private final int value;

    CPUState(int value){
        this.value = value;
    }
    
    static {
        for (CPUState cpuState : CPUState.values()) {
            map.put(cpuState.value, cpuState);
        }
    }
    
    public static CPUState valueOf(int cpuState) {
        return map.get(cpuState);
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
         return String.valueOf(this.value);
    }
    
    public String getName(){
        return this.name();
    }

}
