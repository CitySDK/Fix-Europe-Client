package pt.fix.europe.tasks;

import java.util.ArrayList;

import com.google.gson.JsonSyntaxException;

import open311.base.ServiceRequest;
import open311.exceptions.ServerErrorException;
import open311.requests.Format;
import open311.requests.Open311;
import open311.requests.Parameter;

public class RequestFetcher implements Fetcher {
	private String url;
	private String id;

	public RequestFetcher(String url, String id) {
		this.url = url;
		this.id = id;
	}
	
	@Override
	public Object fetchData(Parameter... parameters) {
		try {
			Open311 stub = Open311.getInstance();
			return stub.getRequestWithId(url, Format.FORMAT_JSON, id, parameters);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (ServerErrorException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<ServiceRequest>();
	}

}
