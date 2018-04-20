package spiNNManClasses.model.enums;

import java.util.HashMap;

/**
 *
 * @author alan
 */
public enum MailBoxCommand {
    SHM_IDLE(0),
    SHM_MSG (1), 
    SHM_NOP(2),
    SHM_SIGNAL(3),
    SHM_CMD(4);
    
    private static final HashMap<Integer, MailBoxCommand> map = new HashMap<>();
    private final int value;

    MailBoxCommand(int value){
        this.value = value;
    }
    
    static {
        for (MailBoxCommand cpuState : MailBoxCommand.values()) {
            map.put(cpuState.value, cpuState);
        }
    }
    
    public static MailBoxCommand valueOf(int mailBoxCommand) {
        return map.get(mailBoxCommand);
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
         return String.valueOf(this.value);
    }
}
