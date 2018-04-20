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
public class CoreLocation extends ChipLocation{
    protected final int p;
    
    public CoreLocation(int x, int y, int p){
        super(x, y);
        this.p = p;    
    }
   
    
    @Override
    public boolean equals(Object obj){
        if (this == obj) 
            return true;
        if (!(obj instanceof CoreLocation)) 
            return false;
        
        CoreLocation that = (CoreLocation)obj;
        return this.equals(that);    
    }

    @Override
    public int hashCode(){
        return this.x + this.y + this.p;
    }

    public int compareTo(CoreLocation that){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        if (this.x == that.get_x() && (this.y == that.get_y()) && 
                (this.p == that.get_p())){
            return 0;
        }
        return 1;
    }
    
    public int get_p(){
        return this.p;
    }
}
