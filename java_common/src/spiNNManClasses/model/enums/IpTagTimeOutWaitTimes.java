package spiNNManClasses.model.enums;

import java.util.HashMap;

public enum IpTagTimeOutWaitTimes {
    // the values used by the SCP iptag time outs. These control how long to wait
    // for any message request which requires a response, before raising an error.
    // The value is calculated via the following formulae
    // 10ms * 2^(tag_timeout_value - 1)
    
    TIMEOUT_10_ms(1),
    TIMEOUT_20_ms(2),
    TIMEOUT_40_ms(3),
    TIMEOUT_80_ms(4),
    TIMEOUT_160_ms(5),
    TIMEOUT_320_ms(6),
    TIMEOUT_640_ms(7),
    TIMEOUT_1280_ms(8),
    TIMEOUT_2560_ms(9);

    private static final HashMap<Integer, IpTagTimeOutWaitTimes> map = 
        new HashMap<>();
    private final int value;

    IpTagTimeOutWaitTimes(int value){
        this.value = value;
    }
    
    static {
        for (IpTagTimeOutWaitTimes iptag_time_out_wait_time : 
                IpTagTimeOutWaitTimes.values()) {
            map.put(iptag_time_out_wait_time.value, iptag_time_out_wait_time);
        }
    }
    
    public static IpTagTimeOutWaitTimes valueOf(int iptag_time_out_wait_time) {
        return map.get(iptag_time_out_wait_time);
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
