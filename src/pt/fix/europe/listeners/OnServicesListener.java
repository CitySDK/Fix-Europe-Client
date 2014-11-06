package pt.fix.europe.listeners;

import java.util.List;

import open311.base.Service;

public abstract class OnServicesListener implements OnDataListener {
	
	@SuppressWarnings("unchecked")
	@Override
	public void onDataReceived(Object object) {
		onServicesReceived((List<Service>) object);
	}
	
	public abstract void onServicesReceived(List<Service> services);
}
