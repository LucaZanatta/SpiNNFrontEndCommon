package spiNNManClasses.model.enums;

import java.util.HashMap;

public enum EIEIOCommandIDs {
    DATABASE_CONFIRMATION(1),
    EVENT_PADDING(2),
    EVENT_STOP(3),
    STOP_SENDING_REQUESTS(4),
    START_SENDING_REQUESTS(5),
    SPINNAKER_REQUEST_BUFFERS(6),
    HOST_SEND_SEQUENCED_DATA(7),
    SPINNAKER_REQUEST_READ_DATA(8),
    HOST_DATA_READ(9),
    STOP_PAUSE_NOTIFICATION(10),
    START_RESUME_NOTIFICATION(11),
    HOST_DATA_READ_ACK(12);

    private static final HashMap<Integer, EIEIOCommandIDs> map = 
        new HashMap<>();
    private final int value;

    EIEIOCommandIDs(int value){
        this.value = value;
    }
    
    static {
        for (EIEIOCommandIDs eieioCommandId : EIEIOCommandIDs.values()) {
            map.put(eieioCommandId.value, eieioCommandId);
        }
    }
    
    public static EIEIOCommandIDs valueOf(int eieioCommandId) {
        return map.get(eieioCommandId);
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
