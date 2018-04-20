package commonClasses;

/**
 * 
 * @author dkf
 */
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
        return (this.x == that.x) && (this.y == that.y) && (this.p == that.p)
                && (this.regionID == that.regionID);
    }

    @Override
    public int hashCode() {
        return (((((x << 4) ^ y) << 4) ^ p) << 3) ^ regionID;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final int getP() {
        return p;
    }

    @Override
    public final int getRegionID() {
        return regionID;
    }
}
