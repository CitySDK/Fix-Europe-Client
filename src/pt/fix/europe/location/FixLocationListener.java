package pt.fix.europe.location;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class FixLocationListener implements LocationListener {
	private static FixLocationListener instance = null;
	public double latitude;
	public double longitude;
	public float accuracy;
	private List<PositionListener> listeners;
	
	private FixLocationListener() { 
		this.listeners = new ArrayList<PositionListener>();
	}
	
	public synchronized static FixLocationListener getInstance() {
		if(instance == null)
			instance = new FixLocationListener();
		
		return instance;
	}
	
	public void subscribeToLocations(PositionListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		for(PositionListener listener : listeners) 
			listener.warnPositionChanged(location);
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	public void removeListener(PositionListener listener) {
		listeners.remove(listener);
	}
}
