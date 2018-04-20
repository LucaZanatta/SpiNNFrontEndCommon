package commonClasses;

public interface HasChipLocation {
    /**
     * @return The X coordinate of the chip.
     */
    int getX();

    /**
     * @return The X coordinate of the chip.
     */
    int getY();

    /**
     * Check if two locations are colocated at the chip level. This does
     * <i>not</i> imply that the two are equal.
     * 
     * @param other
     *            The other location to compare to.
     * @return If the two locations have the same X and Y coordinates.
     */
    default public boolean onSameChipAs(HasChipLocation other) {
        return (getX() == other.getX()) && (getY() == other.getY());
    }
}
