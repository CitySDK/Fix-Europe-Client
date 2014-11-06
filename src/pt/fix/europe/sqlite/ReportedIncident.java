package pt.fix.europe.sqlite;

public class ReportedIncident {
	public String serviceRequestId;
	public String serviceRequestDescription;
	public String serviceRequestDatetime;
	public boolean serviceRequestSynched;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ReportedIncident))
            return false;

        ReportedIncident incident = (ReportedIncident) obj;
        return serviceRequestId.equals(incident.serviceRequestId);
	}
}
