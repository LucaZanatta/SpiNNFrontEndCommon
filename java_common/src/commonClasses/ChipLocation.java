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
public class ChipLocation implements HasChipLocation {
    private final int x;
    private final int y;

    public ChipLocation(int x, int y) {
        this.x = x;
        this.y = y;
        // TODO: validate x and y for physical sanity
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChipLocation))
            return false;

        ChipLocation that = (ChipLocation) obj;
        return (this.x == that.x) && (this.y == that.y);
    }

    @Override
    public int hashCode() {
        return (this.x << 4) ^ this.y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}
