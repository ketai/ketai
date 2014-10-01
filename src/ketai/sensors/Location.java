/* 
 * Simple wrapper for android Location class.
 * 
 */

package ketai.sensors;

/**
 * Wrapper for the android location class.
 */
public class Location extends android.location.Location {

	/**
	 * Instantiates a new location.
	 *
	 * @param _loc the _loc
	 */
	public Location(String _loc) {
		super(_loc);
	}

	/**
	 * Instantiates a new location.
	 *
	 * @param l the l
	 */
	public Location(android.location.Location l) {
		super(l);
	}
}
