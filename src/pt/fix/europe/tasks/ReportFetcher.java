package pt.fix.europe.tasks;

import java.util.ArrayList;

import org.codeforamerica.open311.facade.APIWrapper;
import org.codeforamerica.open311.facade.APIWrapperFactory;
import org.codeforamerica.open311.facade.data.POSTServiceRequestResponse;
import org.codeforamerica.open311.facade.data.operations.POSTServiceRequestData;
import org.codeforamerica.open311.facade.exceptions.APIWrapperException;

import com.google.gson.JsonSyntaxException;

import open311.base.Service;
import open311.exceptions.ErrorException;
import open311.requests.Format;
import open311.requests.Open311;
import open311.requests.Parameter;
import open311.response.ServiceRequestResponse;

public class ReportFetcher implements Fetcher {
	private String url;
	private String apiKey;
	private Service service;

	public ReportFetcher(String url, String apiKey, Service service) {
		this.url = url;
		this.apiKey = apiKey;
		this.service = service;
	}

	@Override
	public Object fetchData(Parameter... parameters) {
		try {
			String lat = "";
			String lng = "";
			String description = "";
			for(int i = 0; i < parameters.length; i++) {
				if(parameters[i].getName().equalsIgnoreCase("lat")) {
					lat = parameters[i].getValue();
				} else if(parameters[i].getName().equalsIgnoreCase("long")) {
					lng = parameters[i].getValue();
				}  else if(parameters[i].getName().equalsIgnoreCase("description")) {
					description = parameters[i].getValue();
				}
			}
			APIWrapper wrapperPost = new APIWrapperFactory(url.substring(0, url.length()-1), org.codeforamerica.open311.facade.Format.XML).setApiKey(apiKey).build();
			
			POSTServiceRequestData psrd = new POSTServiceRequestData(service.serviceCode, Float.valueOf(lat), Float.valueOf(lng), null);
			psrd.setDescription(description);
			POSTServiceRequestResponse postServiceRequest = wrapperPost.postServiceRequest(psrd);
			return postServiceRequest;

		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (APIWrapperException e) {
			e.printStackTrace();
		}

		return new ArrayList<ServiceRequestResponse>();
	}

}
