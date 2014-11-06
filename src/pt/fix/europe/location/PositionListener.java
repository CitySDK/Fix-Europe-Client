package pt.fix.europe.location;

import android.location.Location;

public interface PositionListener {
	void warnPositionChanged(Location location);
}
