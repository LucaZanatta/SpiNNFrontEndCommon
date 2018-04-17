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
public class Location implements Comparable<Location>{
    private final int _x;
    private final int _y;
    private final Integer _p;
    private final Integer _region_id;
    
    
    public Location(int x, int y, int p, int region_id){
        this._x = x;
        this._y = y;
        this._p = p;
        this._region_id = region_id;
    }
    
    public Location(int x, int y, int p){
        this._x = x;
        this._y = y;
        this._p = p;    
        this._region_id = null;
    }
    
    public Location(int x, int y){
        this._x = x;
        this._y = y;
        this._p = null;    
        this._region_id = null;
    }
    
    @Override
    public boolean equals(Object obj){
        if (this == obj) 
            return true;
        if (!(obj instanceof Location)) 
            return false;
        
        Location that = (Location)obj;
        return this.equals(that);    
    }

    @Override
    public int hashCode(){
        return this._x + this._y + this._p + this._region_id;
    }

    @Override
    public int compareTo(Location that){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        if ((this._x == that.get_x() && (this._y == that.get_y()) && 
                (this._p == that.get_p()) && 
                (this._region_id == that.get_region()))){
            return 0;
        }
        return 1;
    }
    
    public int get_x(){
        return this._x;
    }
    
    public int get_y(){
        return this._y;
    }
    
    public int get_p(){
        return this._p;
    }
    
    public int get_region(){
        return this._region_id;
    }
}
