package pt.fix.europe.sync;

import java.util.List;

import pt.fix.europe.auth.AuthPreferences;
import pt.fix.europe.helper.MetadataReader;
import pt.fix.europe.sqlite.ReportedIncident;
import pt.fix.europe.sqlite.RequestsDataSource;
import pt.fix.europe.sync.requests.FixRequests;
import pt.fix.europe.sync.requests.GetRequestsMethod;
import pt.fix.europe.sync.requests.PostRequestMethod;
import pt.fix.europe.sync.requests.Request;

import android.content.Context;

import com.google.gson.Gson;

public class FixEuropeSync {
	private Context context;
	private MetadataReader reader;
	private RequestsDataSource dataSource;

	public FixEuropeSync(Context context) {
		this.context = context;
		this.reader = new MetadataReader(this.context);
		this.dataSource = new RequestsDataSource(this.context);
	}
	
	public void getAllReports(final OnSyncCompleted listener) {
		AuthPreferences prefs = new AuthPreferences(context);
		String user = prefs.getUser();
		String token = prefs.getToken();
		String uri = reader.getFixEuropeUri();
		SyncTask task = new SyncTask(new GetRequestsMethod(user, token, uri),
				new OnSyncFinishedListener() {

					@Override
					public void onSyncFinished(int code, String json) {
						if (code == 200) {
							FixRequests requests = new Gson().fromJson(json,
									FixRequests.class);
							List<Request> list = requests.getRequests();
							dataSource.open();
							for (Request request : list) {
								dataSource.createIncident(request
										.getServiceRequestId(), request
										.getServiceRequestDescription(),
										request.getServiceRequestDatetime()
												.toString(), true);
							}
							dataSource.close();
							
							listener.onSyncCompleted();
						}
					}
				});
		task.execute();
	}
	
	public void syncRequest(final String requestId, String serviceNotice) {
		AuthPreferences prefs = new AuthPreferences(context);
		String user = prefs.getUser();
		String token = prefs.getToken();
		String uri = reader.getFixEuropeUri();
		PostRequestMethod method = new PostRequestMethod(user, token, uri);
		method.setRequest(requestId, serviceNotice);
		
		SyncTask task = new SyncTask(method, new OnSyncFinishedListener() {
			
			@Override
			public void onSyncFinished(int code, String result) {
				if (code == 200) {
					dataSource.open();
					dataSource.requestSynched(requestId);
					dataSource.close();
				}
			}
		});
		task.execute();
	}
	
	public void syncUnsynched() {
		dataSource.open();
		List<ReportedIncident> unSynched = dataSource.getAllUnsynched();
		for (ReportedIncident incident : unSynched) {
			syncRequest(incident.serviceRequestId, "");
		}
		dataSource.close();
	}
}
