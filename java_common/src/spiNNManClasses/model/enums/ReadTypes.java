package spiNNManClasses.model.enums;

import java.util.HashMap;

public enum ReadTypes {
    // the types of read available from SARK. These values are used to tell 
    //SARK how to read the data in a time efficient manner.
    
    BYTE(0),
    HALF_WORD(1),
    WORD(2);

    private static final HashMap<Integer, ReadTypes> map = new HashMap<>();
    private final int value;

    ReadTypes(int value){
        this.value = value;
    }
    
    static {
        for (ReadTypes readType : ReadTypes.values()) {
            map.put(readType.value, readType);
        }
    }
    
    public static ReadTypes valueOf(int readType) {
        return map.get(readType);
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
