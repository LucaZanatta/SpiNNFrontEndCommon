/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonClasses;

/**
 *
 * @author alan
 */
public class ChipLocation implements Comparable<ChipLocation>{
    protected final int x;
    protected final int y;
    
    
    public ChipLocation(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj){
        if (this == obj) 
            return true;
        if (!(obj instanceof ChipLocation)) 
            return false;
        
        ChipLocation that = (ChipLocation)obj;
        return this.equals(that);    
    }

    @Override
    public int hashCode(){
        return this.x + this.y;
    }

    @Override
    public int compareTo(ChipLocation that){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        if (this.x == that.get_x() && (this.y == that.get_y())){
            return 0;
        }
        return 1;
    }
    
    public int get_x(){
        return this.x;
    }
    
    public int get_y(){
        return this.y;
    }
}
