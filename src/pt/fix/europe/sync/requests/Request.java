package pt.fix.europe.sync.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Basic request sent by the user
 * 
 * @author Pedro Cruz
 *
 */
public class Request {
	public static final transient String SERVICE_REQUEST_ID = "service_request_id";
	public static final transient String SERVICE_REQUEST_NOTICE = "service_request_notice";
	public static final transient String SERVICE_REQUEST_USER = "service_request_user";
	public static final transient String SERVICE_REQUEST_DESCRIPTION = "service_request_description";
	public static final transient String SERVICE_REQUEST_DATETIME = "service_request_datetime";
	public static final transient String SERVICE_REQUEST_STATUS = "service_request_status";
	public static final transient String SERVICE_REQUEST_LATITUDE = "service_request_latitude";
	public static final transient String SERVICE_REQUEST_LONGITUDE = "service_request_longitude";
	public static final transient String SERVICE_REQUEST_SERVICE_CODE = "service_request_service_code";
	
	@SerializedName(SERVICE_REQUEST_ID)
	private String serviceRequestId;
	@SerializedName(SERVICE_REQUEST_NOTICE)
	private String serviceRequestNotice;
	@SerializedName(SERVICE_REQUEST_DESCRIPTION)
	private String serviceRequestDescription;
	@SerializedName(SERVICE_REQUEST_DATETIME)
	private String serviceRequestDatetime;
	@SerializedName(SERVICE_REQUEST_LATITUDE)
	private String serviceRequestLatitude;
	@SerializedName(SERVICE_REQUEST_LONGITUDE)
	private String serviceRequestLongitude;
	@SerializedName(SERVICE_REQUEST_STATUS)
	private String serviceRequestStatus;

	public Request() { 
		this.serviceRequestId = "";
		this.serviceRequestNotice = "";
		this.serviceRequestLatitude = "";
		this.serviceRequestLongitude = "";
		this.serviceRequestDescription = "";
		this.serviceRequestStatus = "";
	}

	public String getServiceRequestId() {
		return serviceRequestId;
	}

	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}

	public String getServiceRequestNotice() {
		return serviceRequestNotice;
	}

	public void setServiceRequestNotice(String serviceRequestNotice) {
		this.serviceRequestNotice = serviceRequestNotice;
	}
	
	public String getServiceRequestDescription() {
		return serviceRequestDescription;
	}

	public void setServiceRequestDescription(String serviceRequestDescription) {
		this.serviceRequestDescription = serviceRequestDescription;
	}

	public String getServiceRequestDatetime() {
		return serviceRequestDatetime;
	}

	public String getServiceRequestLatitude() {
		return serviceRequestLatitude;
	}

	public String getServiceRequestLongitude() {
		return serviceRequestLongitude;
	}

	public void setServiceRequestDatetime(String serviceRequestDatetime) {
		this.serviceRequestDatetime = serviceRequestDatetime;
	}

	public void setServiceRequestLatitude(String serviceRequestLatitude) {
		this.serviceRequestLatitude = serviceRequestLatitude;
	}

	public void setServiceRequestLongitude(String serviceRequestLongitude) {
		this.serviceRequestLongitude = serviceRequestLongitude;
	}

	public String getServiceRequestStatus() {
		return serviceRequestStatus;
	}

	public void setServiceRequestStatus(String serviceRequestStatus) {
		this.serviceRequestStatus = serviceRequestStatus;
	}
}
