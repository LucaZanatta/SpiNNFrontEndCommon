package commonClasses;

public class RegionLocation extends CoreLocation{
    private final int regionID;
    
    public RegionLocation(int x, int y, int p, int regionID){
        super(x, y, p);
        this.regionID = regionID;    
    }
   
    
    @Override
    public boolean equals(Object obj){
        if (this == obj) 
            return true;
        if (!(obj instanceof RegionLocation)) 
            return false;
        
        RegionLocation that = (RegionLocation)obj;
        return this.equals(that);    
    }

    @Override
    public int hashCode(){
        return this.x + this.y + this.p + this.regionID;
    }

    public int compareTo(RegionLocation that){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        if ((this.x == that.get_x()) && (this.y == that.get_y()) && 
                (this.p == that.get_p()) &&
                (this.regionID == that.getRegionID())){
            return 0;
        }
        return 1;
    }
    
    public int getRegionID(){
        return this.regionID;
    }
}
