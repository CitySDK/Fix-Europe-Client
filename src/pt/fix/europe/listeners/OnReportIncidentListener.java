package pt.fix.europe.listeners;

import org.codeforamerica.open311.facade.data.POSTServiceRequestResponse;

import open311.response.ServiceRequestResponse;

public abstract class OnReportIncidentListener implements OnDataListener {

	@Override
	public void onDataReceived(Object object) {
		if(object instanceof POSTServiceRequestResponse) {
			onIncidentReported((POSTServiceRequestResponse) object);
		} else {
			onIncidentReported(null);
		}
	}

	public abstract void onIncidentReported(POSTServiceRequestResponse response);
}
