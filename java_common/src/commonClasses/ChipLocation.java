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
public class ChipLocation{
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
        return ((this.x == that.get_x()) && (this.y == that.get_y())); 
    }

    @Override
    public int hashCode(){
        return this.x + this.y;
    }

    public int get_x(){
        return this.x;
    }
    
    public int get_y(){
        return this.y;
    }
}
