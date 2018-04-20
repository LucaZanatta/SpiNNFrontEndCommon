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
public class CoreLocation implements HasCoreLocation {
    private final int x;
    private final int y;
    private final int p;

    public CoreLocation(int x, int y, int p) {
        this.x = x;
        this.y = y;
        this.p = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CoreLocation))
            return false;

        CoreLocation that = (CoreLocation) obj;
        return (this.x == that.x) && (this.y == that.y) && (this.p == that.p);
    }

    @Override
    public int hashCode() {
        return (((this.x << 4) ^ this.y) << 4) ^ this.p;
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
}
