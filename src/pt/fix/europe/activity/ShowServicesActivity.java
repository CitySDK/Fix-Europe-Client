package pt.fix.europe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import open311.base.Service;
import pt.fix.europe.R;
import pt.fix.europe.adapter.ServiceItem;
import pt.fix.europe.adapter.ServicesAdapter;
import pt.fix.europe.sqlite.DataWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ShowServicesActivity extends SherlockActivity {
	private ListView groupListView;
	private ListView listView;
	private List<ServiceItem> services;
	private List<ServiceItem> groups;
	private Map<String, List<Service>> groupMaps;
	private boolean isGroupView;
	private Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_services_activity);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra(FixEuropeActivity.SERVICE);
		List<Service> list = dw.getServices();
		
		listView = (ListView) findViewById(R.id.listview);
		groupListView = (ListView) findViewById(R.id.pinned_list);
		
		prepareData(list);
		
		listView.setAdapter(new ServicesAdapter(this, R.layout.service_item, services));
		groupListView.setAdapter(new ServicesAdapter(this, R.layout.service_item, groups));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				returnService(services.get(position).title, services.get(position).serviceCode);
			}
		});
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				returnService(groups.get(position).title, groups.get(position).serviceCode);
			}
		});
		
		Toast.makeText(this, getResources().getString(R.string.choose_service), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_show_services, menu);
		
		this.menu = menu;
		
		isGroupView = false;
		viewServices();
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
			
		case R.id.view:
			changeView();
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	private void returnService(String title, String serviceCode) {
		Intent intent = new Intent(this, FixEuropeActivity.class);
		intent.putExtra(FixEuropeActivity.SERVICE, title);
		intent.putExtra(FixEuropeActivity.SERVICE_CODE, serviceCode);
		setResult(FixEuropeActivity.CHOOSE_SERVICE, intent);
		finish();
	}
	
	private void changeView() {
		if(isGroupView)
			viewServices();
		else
			viewGroups();
	}

	private void viewServices() {
		menu.getItem(0).setTitle(R.string.menu_group);
		listView.setVisibility(View.VISIBLE);
		groupListView.setVisibility(View.GONE);
		isGroupView = false;
	}
	
	private void viewGroups() {
		menu.getItem(0).setTitle(R.string.menu_single);
		listView.setVisibility(View.GONE);
		groupListView.setVisibility(View.VISIBLE);
		isGroupView = true;
	}
	
	private void prepareData(List<Service> list) {
		if(services != null)
			return;
		
		services = new ArrayList<ServiceItem>();
		groups = new ArrayList<ServiceItem>();
		groupMaps = new HashMap<String, List<Service>>();

		for(Service service : list) {
			services.add(new ServiceItem(ServiceItem.ITEM, service.description, service.serviceName, service.serviceCode));
			if(!groupMaps.containsKey(service.group))
				groupMaps.put(service.group, new ArrayList<Service>());
			
			groupMaps.get(service.group).add(service);
		}
		
		Set<String> groupSet = groupMaps.keySet();
		for(String group : groupSet) {
			if(!groups.contains(group)) {
				groups.add(new ServiceItem(ServiceItem.SECTION, "", group, ""));
			}
			
			List<Service> services = groupMaps.get(group);
			for(Service service : services) {
				groups.add(new ServiceItem(ServiceItem.ITEM, service.description, service.serviceName, service.serviceCode));
			}
		}
	}
}
