package pt.fix.europe.adapter;

import java.util.ArrayList;
import java.util.List;

import open311.base.Service;
import pt.fix.europe.R;
import pt.fix.europe.helper.StringScore;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class AutoCompleteServicesAdapter extends ArrayAdapter<Service> {  
    private int resourceId;
    private List<Service> origData;
    private List<Service> data;
    
	public AutoCompleteServicesAdapter(Context context, int resource, int textViewResourceId, List<Service> objects) {
		super(context, resource, textViewResourceId, objects);
        this.resourceId = resource;
        this.data = objects;
        this.origData = new ArrayList<Service>(objects);
	}
	
	public int getCount() {
		return data.size();
	}

	public Service getItem(int position) {
		return data.get(position);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resourceId, null);
        }
        
        Service service = data.get(position);
        if (service != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.serviceNameLabel);
            if (customerNameLabel != null) {
                customerNameLabel.setText(String.valueOf(service.serviceName));
            }
        }
        return v;
    }
    
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	Filter filter = new Filter() {
		
        public String convertResultToString(Object resultValue) {
            String str = ((Service)(resultValue)).serviceName; 
            return str;
        }
        
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
        	FilterResults filterResults = new FilterResults();
            if(constraint != null) {
                List<Service> filteredItems = new ArrayList<Service>();
                
                for (Service service : data) {
                	if(StringScore.score(service.serviceName, constraint.toString()) >= 0.4
                    		|| StringScore.score(service.keywords, constraint.toString()) >= 0.3) {
                		filteredItems.add(service);
                	}
                }
                
                filterResults.values = filteredItems;
                filterResults.count = filteredItems.size();
            }
            
            return filterResults;
        }
        
		@SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null && results.count > 0) {
                data = (List<Service>) results.values;
                notifyDataSetChanged();
            } else {
            	data = origData;
            	notifyDataSetInvalidated();
            }   
        }
    };
    
    static class ServiceHolder {
        TextView txtTitle;
    }
	
}
