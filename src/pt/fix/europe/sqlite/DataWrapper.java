package pt.fix.europe.sqlite;

import java.io.Serializable;
import java.util.List;

import open311.base.Service;

public class DataWrapper implements Serializable {
	private static final long serialVersionUID = -1112001699427475544L;
	private List<Service> services;
	
	public DataWrapper(List<Service> services) {
		this.services = services;
	}
	
	public List<Service> getServices() {
		return services;
	}
}
