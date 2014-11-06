package pt.fix.europe.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import open311.base.ServiceRequest;
import open311.requests.Parameter;
import open311.requests.Requests;
import pt.fix.europe.R;
import pt.fix.europe.adapter.IncidentRequest;
import pt.fix.europe.adapter.IncidentsAdapter;
import pt.fix.europe.auth.AuthPreferences;
import pt.fix.europe.helper.MetadataReader;
import pt.fix.europe.listeners.OnRequestListener;
import pt.fix.europe.sqlite.ReportedIncident;
import pt.fix.europe.sqlite.RequestsDataSource;
import pt.fix.europe.sync.FixEuropeSync;
import pt.fix.europe.sync.OnSyncCompleted;
import pt.fix.europe.sync.OnSyncFinishedListener;
import pt.fix.europe.sync.SyncPreferences;
import pt.fix.europe.sync.SyncTask;
import pt.fix.europe.sync.requests.FixRequests;
import pt.fix.europe.sync.requests.GetRequestsMethod;
import pt.fix.europe.sync.requests.Request;
import pt.fix.europe.tasks.CitySDKSyncTask;
import pt.fix.europe.tasks.RequestFetcher;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;

public class ReportedIncidentsActivity extends SherlockActivity {
	private List<IncidentRequest> requests;
	private List<ServiceRequest> serviceRequests;
	private List<ReportedIncident> data;
	private ListView list;
	private ProgressBar bar;
	private RequestsDataSource dataSource;
	private IncidentsAdapter adapter;
	private View header;
	private SyncPreferences syncPrefs;
	private MetadataReader reader;

	private final String RECEIVED = "RECEIVED";
	private final String IN_PROCESS = "IN_PROCESS";
	private final String PROCESSED = "PROCESSED";
	private final String ARCHIVED = "ARCHIVED";
	private final String REJECTED = "REJECTED";
	private final String PHOTO = "PHOTO";
	private final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reported_incidents_activity);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		LayoutInflater inflater = getLayoutInflater();

		syncPrefs = new SyncPreferences(this);
		reader = new MetadataReader(this);
		list = (ListView) findViewById(R.id.listview);
		bar = (ProgressBar) findViewById(R.id.progress_small1);

		serviceRequests = new ArrayList<ServiceRequest>();
		requests = new ArrayList<IncidentRequest>();

		adapter = new IncidentsAdapter(this.getApplicationContext(),
				R.layout.list_item, requests);

		header = inflater.inflate(R.layout.header, null);
		header.setClickable(false);

		list.addHeaderView(header);
		list.setAdapter(adapter);
		list.setEmptyView(findViewById(R.id.empty_list_item));

		showProgress();
		getRequests();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getRequests() {
		DateTime lastSync = syncPrefs.getLastSync();
		if (lastSync != null)
			Log.i(TAG, "Last Sync: " + lastSync + " need sync? "
					+ !lastSync.plusHours(24).isAfter(DateTime.now()));

		dataSource = new RequestsDataSource(this);

		if (lastSync == null
				|| (lastSync != null && !lastSync.plusHours(24).isAfter(
						DateTime.now()))) {
			dataSource.open();
			dataSource.deleteAllIncidents();
			dataSource.close();
			syncWithFixEurope();
			syncPrefs.setLastSync(DateTime.now());
		} else {
			syncWithCitySDK();
		}
	}

	private void syncWithFixEurope() {
		FixEuropeSync sync = new FixEuropeSync(this);
		sync.getAllReports(new OnSyncCompleted() {
			
			@Override
			public void onSyncCompleted() {
				syncWithCitySDK();
			}
		});
	}

	private void syncWithCitySDK() {
		dataSource.open();

		data = dataSource.getAllIncidents();
		hideProgress();

		if (data.size() == 0) {
			dataSource.close();
			return;
		} else {
			TextView h = (TextView) header.findViewById(R.id.header);
			h.setText(getResources().getString(R.string.loading));
		}

		String url = reader.getCitySDKUri();
		for (ReportedIncident reported : data) {
			RequestFetcher fetcher = new RequestFetcher(url,
					reported.serviceRequestId);
			CitySDKSyncTask task = new CitySDKSyncTask(fetcher,
					new OnRequestListener() {

						@Override
						public void onServiceRequestReceived(
								List<ServiceRequest> list) {
							addRequests(list);
						}
					});
			task.execute(new Parameter(Requests.EXTENSIONS, "true"));
		}

		dataSource.close();
	}

	private void addRequests(List<ServiceRequest> list) {
		TextView h = (TextView) header.findViewById(R.id.header);
		h.setText(getResources().getString(R.string.showing) + " "
				+ data.size() + " "
				+ getResources().getString(R.string.results));

		for (ServiceRequest request : list) {
			serviceRequests.add(request);
			ReportedIncident r = new ReportedIncident();
			r.serviceRequestId = request.serviceRequestId;

			String description = getResources().getString(
					R.string.information_unavailable), serviceName = getResources()
					.getString(R.string.information_unavailable);
			if (request.requestedDatetime != null) { 
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") {
						private static final long serialVersionUID = -6162551464502711349L;

						public Date parse(String source, ParsePosition pos) {
							return super.parse(source.replaceFirst(":(?=[0-9]{2}$)", ""),
									pos);
						}
					};
					serviceName = df.parse(request.requestedDatetime).toString();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			if (request.description != null)
				description = request.description;

			IncidentRequest incident;
			if (request.extendedAttributes != null) {
				incident = new IncidentRequest(serviceName,
					description,
					getBitmap(request.extendedAttributes.detailedStatus));
			} else {
				incident = new IncidentRequest(serviceName,
						description,
						getBitmap(""));
			}
			
			requests.add(incident);
		}

		adapter.notifyDataSetChanged();
	}

	private Bitmap getBitmap(String detailedStatus) {
		int drawableId;
		if (detailedStatus.equals(RECEIVED)) {
			drawableId = R.drawable.ic_received;
		} else if (detailedStatus.equals(IN_PROCESS)) {
			drawableId = R.drawable.ic_in_process;
		} else if (detailedStatus.equals(PROCESSED)) {
			drawableId = R.drawable.ic_processed;
		} else if (detailedStatus.equals(ARCHIVED)) {
			drawableId = R.drawable.ic_archived;
		} else if (detailedStatus.equals(REJECTED)) {
			drawableId = R.drawable.ic_rejected;
		} else if (detailedStatus.equals(PHOTO)) {
			drawableId = R.drawable.ic_photo;
		} else {
			drawableId = R.drawable.ic_launcher;
		}

		Bitmap bitmap = BitmapFactory
				.decodeResource(getResources(), drawableId);
		return Bitmap.createScaledBitmap(bitmap, 140, 140, false);
	}

	private Bitmap getPicture(String uri) {
		if (uri == null)
			return getBitmap(PHOTO);

		Bitmap bitmap = BitmapFactory.decodeFile(uri);
		return Bitmap.createScaledBitmap(bitmap, 140, 140, false);
	}

	private void showProgress() {
		bar.setVisibility(View.VISIBLE);
		list.setVisibility(View.GONE);
	}

	private void hideProgress() {
		bar.setVisibility(View.GONE);
		list.setVisibility(View.VISIBLE);
	}
}
