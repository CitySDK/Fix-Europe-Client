package pt.fix.europe.activity;

import java.util.ArrayList;
import java.util.List;

import org.codeforamerica.open311.facade.data.POSTServiceRequestResponse;

import open311.base.BinaryInfo;
import open311.base.Service;
import open311.exceptions.ErrorException;
import open311.requests.Parameter;
import open311.requests.Requests;
import open311.response.ServiceRequestResponse;
import pt.fix.europe.R;
import pt.fix.europe.adapter.AutoCompleteServicesAdapter;
import pt.fix.europe.helper.MetadataReader;
import pt.fix.europe.helper.PhotoContainer;
import pt.fix.europe.helper.StringScore;
import pt.fix.europe.listeners.OnReportIncidentListener;
import pt.fix.europe.listeners.OnServicesListener;
import pt.fix.europe.location.FixLocationListener;
import pt.fix.europe.location.PositionListener;
import pt.fix.europe.sqlite.DataWrapper;
import pt.fix.europe.sqlite.RequestsDataSource;
import pt.fix.europe.sync.FixEuropeSync;
import pt.fix.europe.tasks.CitySDKSyncTask;
import pt.fix.europe.tasks.ReportFetcher;
import pt.fix.europe.tasks.ServiceFetcher;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FixEuropeActivity extends SherlockActivity implements
		PositionListener {
	public static final int CHOOSE_SERVICE = 0;
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final String SERVICE = "service";
	public static final String SERVICE_CODE = "serviceCode";

	private RelativeLayout pictureLayout;
	private TextView incidentDescriptionTitle;
	private EditText incidentDescription;
	private TextView incidentServiceTitle;
	private AutoCompleteTextView incidentService;
	private TextView showServices;
	private Button reportButton;
	private TextView incidentAddressTitle;
	private EditText incidentAddress;
	private TextView incidentLocation;
	private TextView pictureTitle;
	private ProgressBar bar;
	private AutoCompleteServicesAdapter servicesAdapter;
	private List<Service> services;

	private RequestsDataSource dataSource;
	private LocationManager locationManager;
	private FixLocationListener listener;
	private Location location;
	private PhotoContainer photo;

	private Service service = null;
	private String serviceDescription = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_incident_activity);

		pictureLayout = (RelativeLayout) findViewById(R.id.pic_layout);
		pictureTitle = (TextView) findViewById(R.id.picture);
		incidentDescriptionTitle = (TextView) findViewById(R.id.incident_description_title);
		incidentDescription = (EditText) findViewById(R.id.incident_description);
		incidentServiceTitle = (TextView) findViewById(R.id.incident_service_title);
		incidentService = (AutoCompleteTextView) findViewById(R.id.incident_service);
		incidentAddressTitle = (TextView) findViewById(R.id.incident_address_title);
		incidentAddress = (EditText) findViewById(R.id.incident_address);
		incidentLocation = (TextView) findViewById(R.id.incident_location);
		showServices = (TextView) findViewById(R.id.show_services);
		reportButton = (Button) findViewById(R.id.report_button);
		bar = (ProgressBar) findViewById(R.id.progress_small1);

		pictureLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				photo = new PhotoContainer();

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri fileUri = photo
						.getOutputMediaFileUri(PhotoContainer.MEDIA_TYPE_IMAGE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});

		showServices.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FixEuropeActivity.this,
						ShowServicesActivity.class);
				intent.putExtra(FixEuropeActivity.SERVICE, new DataWrapper(
						services));
				FixEuropeActivity.this.startActivityForResult(intent,
						CHOOSE_SERVICE);
			}
		});

		reportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					serviceDescription = incidentDescription.getText()
							.toString();
					reportIncident();
				} catch (ErrorException e) {
					Toast.makeText(FixEuropeActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		incidentService.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				service = services.get(position);
			}
		});

		hideFields();
		startLocation();

		MetadataReader reader = new MetadataReader(this.getApplicationContext());
		CitySDKSyncTask task = new CitySDKSyncTask(new ServiceFetcher(
				reader.getCitySDKUri()), new OnServicesListener() {

			@Override
			public void onServicesReceived(List<Service> services) {
				readServices(services);
			}
		});
		task.execute(new Parameter[0]);

		dataSource = new RequestsDataSource(this);

		FixEuropeSync sync = new FixEuropeSync(this);
		sync.syncUnsynched();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_report_incident, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.action_settings:
			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_incidents:
			intent = new Intent(this, ReportedIncidentsActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		FixLocationListener.getInstance().removeListener(this);
		super.onDestroy();
	}

	@Override
	public void warnPositionChanged(Location location) {
		this.location = location;
		incidentLocation.setText(getResources().getString(
				R.string.location_pinpointed)
				+ "\n"
				+ location.getLatitude()
				+ "; "
				+ location.getLongitude());
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == CHOOSE_SERVICE) {
			handleService(resultCode, data);
		}

		if (resultCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			handlePicture(resultCode, data);
		}
	}

	private void handleService(int resultCode, Intent data) {
		if (data != null && data.getExtras() != null) {
			String serviceName = data.getExtras().getString(
					FixEuropeActivity.SERVICE);

			try {
				incidentService.setText(serviceName);
				service = searchService(serviceName);
				Toast.makeText(getApplicationContext(), serviceName,
						Toast.LENGTH_LONG).show();
			} catch (ErrorException e) {
				e.printStackTrace();
			}
		}
	}

	private void handlePicture(int resultCode, Intent data) {
		String title = pictureTitle.getText().toString();
		if (resultCode == RESULT_OK) {
			Toast.makeText(this, getResources().getString(R.string.photo_ok),
					Toast.LENGTH_LONG).show();
			title += " (" + getResources().getString(R.string.photo_ok) + ")";
		} else if (resultCode == RESULT_CANCELED) {
			photo.deleteFile();
			Log.i("Report", "Deleted photo");
			Toast.makeText(this,
					getResources().getString(R.string.photo_cancel),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.photo_error),
					Toast.LENGTH_LONG).show();
		}

		pictureTitle.setText(title);
	}

	private void startLocation() {
		listener = FixLocationListener.getInstance();
		listener.subscribeToLocations(this);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, listener);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, listener);
	}

	private void reportIncident() throws ErrorException {
		if (service == null && !incidentService.getText().toString().equals("")) {
			service = searchService(incidentService.getText().toString());
		}

		if (service == null) {
			throw new ErrorException(getResources().getString(
					R.string.service_unknown));
		}

		if (serviceDescription == null) {
			throw new ErrorException(getResources().getString(
					R.string.service_error));
		}

		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(Requests.DESCRIPTION, serviceDescription));

		checkLocation(parameters);
		checkEmailAccount(parameters);
		checkPhoneNumber(parameters);
		checkPhoto(parameters);

		parameters.add(new Parameter("account_id", "teste.pt"));

		MetadataReader reader = new MetadataReader(this.getApplicationContext());
		
		//FIX THIS!
		CitySDKSyncTask task = new CitySDKSyncTask(new ReportFetcher(
				reader.getCitySDKUri(), reader.getCitySDKKey(), service),
				new OnReportIncidentListener() {

					@Override
					public void onIncidentReported(
							POSTServiceRequestResponse response) {
						if (response != null)
							saveRequestResponse(response);
						else
							Toast.makeText(
									FixEuropeActivity.this,
									getResources().getString(
											R.string.report_error),
									Toast.LENGTH_SHORT).show();						
					}
				});
		task.execute(parameters.toArray(new Parameter[parameters.size()]));
		service = null;
		serviceDescription = null;
	}

	private void checkPhoto(List<Parameter> parameters) {
		if (photo == null)
			return;

		Uri fileUri = photo
				.getOutputMediaFileUri(PhotoContainer.MEDIA_TYPE_IMAGE);
		if (fileUri == null)
			return;

		BinaryInfo info = new BinaryInfo(photo.getBytes(fileUri), "image/jpg",
				photo.getFilename());
		Parameter parameter = new Parameter(Requests.MEDIA, null);
		parameter.setBinaryInfo(info);

		parameters.add(parameter);
	}

	private void saveRequestResponse(POSTServiceRequestResponse response) {
		dataSource.open();
		Uri fileUri = null;
		if (photo != null)
			fileUri = photo
					.getOutputMediaFileUri(PhotoContainer.MEDIA_TYPE_IMAGE);
		String pic = null;
		if (fileUri != null)
			pic = fileUri.getPath();

		// TODO: pic to be synched
		
		dataSource.createIncident(response.getServiceRequestId(),
				response.getAccountId(), response.getServiceNotice(), false);

		FixEuropeSync sync = new FixEuropeSync(this);
		sync.syncRequest(response.getServiceRequestId(), response.getServiceNotice());

		dataSource.close();

		Toast.makeText(this, getResources().getString(R.string.report_ok),
				Toast.LENGTH_SHORT).show();
	}

	private void checkEmailAccount(List<Parameter> list) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sp.getBoolean("checkbox_mail_report_preferences", false)) {
			String email = sp.getString("prefered_email", "");
			list.add(new Parameter(Requests.EMAIL, email));
			Log.i("ReportIncident", "Attached email " + email);
		}
	}

	private void checkLocation(List<Parameter> list) {
		//list.add(new Parameter(Requests.LATITUDE, "38.708085"));
		//list.add(new Parameter(Requests.LONGITUDE, "-9.138762"));
		list.add(new Parameter(Requests.LATITUDE, "" +
				location.getLatitude()));
		list.add(new Parameter(Requests.LONGITUDE, "" +
				location.getLongitude()));
		Log.i("ReportIncident", "Attached location " + location.getLatitude()
				+ "; " + location.getLongitude());
	}

	private void checkPhoneNumber(List<Parameter> list) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sp.getBoolean("checkbox_phone_report_preferences", false)) {
			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			list.add(new Parameter(Requests.PHONE, tMgr.getLine1Number()));
			Log.i("ReportIncident", "Attached phone " + tMgr.getLine1Number());
		}
	}

	private Service searchService(String s) throws ErrorException {
		System.out.println("searching " + s);
		for (Service service : services) {
			if (StringScore.score(service.serviceName, s) == 1
					|| StringScore.score(service.serviceCode, s) == 1)
				return service;
		}

		throw new ErrorException(getResources().getString(
				R.string.service_unknown));
	}

	private void readServices(List<Service> services) {
		this.services = services;
		this.servicesAdapter = new AutoCompleteServicesAdapter(this,
				R.layout.service_auto, R.id.serviceNameLabel, this.services);
		incidentService.setAdapter(this.servicesAdapter);
		showFields();
	}

	private void hideFields() {
		this.pictureLayout.setVisibility(View.GONE);
		this.incidentDescriptionTitle.setVisibility(View.GONE);
		this.incidentDescription.setVisibility(View.GONE);
		this.incidentServiceTitle.setVisibility(View.GONE);
		this.incidentService.setVisibility(View.GONE);
		this.incidentAddressTitle.setVisibility(View.GONE);
		this.incidentAddress.setVisibility(View.GONE);
		this.incidentLocation.setVisibility(View.GONE);
		this.showServices.setVisibility(View.GONE);
		this.reportButton.setVisibility(View.GONE);
		this.bar.setVisibility(View.VISIBLE);
	}

	private void showFields() {
		this.pictureLayout.setVisibility(View.VISIBLE);
		this.incidentDescriptionTitle.setVisibility(View.VISIBLE);
		this.incidentDescription.setVisibility(View.VISIBLE);
		this.incidentServiceTitle.setVisibility(View.VISIBLE);
		this.incidentService.setVisibility(View.VISIBLE);
		this.incidentAddressTitle.setVisibility(View.VISIBLE);
		this.incidentAddress.setVisibility(View.VISIBLE);
		this.incidentLocation.setVisibility(View.VISIBLE);
		this.showServices.setVisibility(View.VISIBLE);
		this.reportButton.setVisibility(View.VISIBLE);
		this.bar.setVisibility(View.GONE);
	}
}
