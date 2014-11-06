package pt.fix.europe.tasks;

import java.util.ArrayList;

import open311.base.Service;
import open311.exceptions.ServerErrorException;
import open311.requests.Format;
import open311.requests.Open311;
import open311.requests.Parameter;

public class ServiceFetcher implements Fetcher {
	private String url;

	public ServiceFetcher(String url) {
		this.url = url;
	}
	
	@Override
	public Object fetchData(Parameter... parameters) {
		try {
			Open311 stub = Open311.getInstance();
			return stub.getServiceListFrom(url, Format.FORMAT_JSON, parameters);
		} catch (ServerErrorException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Service>();
	}

}
