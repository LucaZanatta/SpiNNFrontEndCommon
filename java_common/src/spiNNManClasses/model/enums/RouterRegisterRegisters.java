package spiNNManClasses.model.enums;

import java.util.HashMap;

public enum RouterRegisterRegisters {
    LOC_MC(0),
    EXT_MC(1),
    LOC_PP(2),
    EXT_PP(3),
    LOC_NN(4),
    EXT_NN(5),
    LOC_FR(6),
    EXT_FR(7),
    DUMP_MC(8),
    DUMP_PP(9),
    DUMP_NN(10),
    DUMP_FR(11),
    USER_0(12),
    USER_1(13),
    USER_2(14),
    USER_3(15);

    private static final HashMap<Integer, RouterRegisterRegisters> map = 
        new HashMap<>();
    private final int value;

    RouterRegisterRegisters(int value){
        this.value = value;
    }
    
    static {
        for (RouterRegisterRegisters routerRegisterRegisters : 
                RouterRegisterRegisters.values()) {
            map.put(routerRegisterRegisters.value, routerRegisterRegisters);
        }
    }
    
    public static RouterRegisterRegisters valueOf(int routerRegisterRegisters) {
        return map.get(routerRegisterRegisters);
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
