package pt.fix.europe.listeners;

import java.util.List;

import open311.base.ServiceRequest;

public abstract class OnRequestListener implements OnDataListener {

	@SuppressWarnings("unchecked")
	@Override
	public void onDataReceived(Object object) {
		onServiceRequestReceived((List<ServiceRequest>) object);
	}

	public abstract void onServiceRequestReceived(List<ServiceRequest> list);
}
