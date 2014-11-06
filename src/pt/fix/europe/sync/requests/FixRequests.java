package pt.fix.europe.sync.requests;

import java.util.ArrayList;
import java.util.List;

public class FixRequests {
	private List<Request> requests;
	
	public FixRequests() {
		requests = new ArrayList<Request>();
	}
	
	public List<Request> getRequests() {
		return requests;
	}
}
