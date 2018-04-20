package commonClasses;

/**
 * 
 * @author dkf
 */
public interface HasRegionLocation extends HasCoreLocation {
    /**
     * @return The region ID.
     */
    int getRegionID();

    /**
     * Check if two locations are colocated at the region level. This does
     * <i>not</i> imply that the two are equal.
     * 
     * @param other
     *            The other location to compare to.
     * @return If the two locations have the same X, Y, and P coordinates, and
     *         have the same region ID.
     */
    default public boolean inSameRegionAs(HasRegionLocation other) {
        return onSameCoreAs(other) && (getRegionID() == other.getRegionID());
    }
}
