package commonClasses;

public class RegionLocation implements HasRegionLocation {
    private final int x;
    private final int y;
    private final int p;
    private final int regionID;

    public RegionLocation(int x, int y, int p, int regionID) {
        this.x = x;
        this.y = y;
        this.p = p;
        this.regionID = regionID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RegionLocation))
            return false;

        RegionLocation that = (RegionLocation) obj;
        return (this.x == that.x) && (this.y == that.y) && (this.p == that.p) && (this.regionID == that.regionID);
    }

    @Override
    public int hashCode() {
        return (((((this.x << 4) ^ this.y) << 4) ^ this.p) << 3) ^ this.regionID;
    }
    
    @Override
    public int getX() {
        return this.x;
    }
    
    @Override
    public int getY() {
        return this.y;
    }
    
    @Override
    public int getP() {
        return this.p;
    }

    @Override
    public int getRegionID() {
        return this.regionID;
    }
}
