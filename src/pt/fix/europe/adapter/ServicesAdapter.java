package pt.fix.europe.adapter;

import java.util.List;

import pt.fix.europe.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServicesAdapter extends ArrayAdapter<ServiceItem> {
	private List<ServiceItem> services;

	public ServicesAdapter(Context context, int resourceId,
			List<ServiceItem> objects) {
		super(context, resourceId, objects);
		this.services = objects;
	}

	@Override
	public int getCount() {
		return services.size();
	}

	@Override
	public ServiceItem getItem(int position) {
		return services.get(position);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		ServiceItem item = services.get(position);
		if (item.isHeader()) {
			return ServiceItem.SECTION;
		} else {
			return ServiceItem.ITEM;
		}
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ServiceViewHolder viewHolder = null;
		ServiceItem item = services.get(position);

		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			if (item.type == ServiceItem.SECTION) {
				v = li.inflate(R.layout.service_section, parent, false);

				viewHolder = new ServiceViewHolder(
						(TextView) v.findViewById(R.id.list_header_title), null);
				
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
			} else {
				v = li.inflate(R.layout.service_item, parent, false);

				viewHolder = new ServiceViewHolder(
						(TextView) v.findViewById(R.id.list_entry_title),
						(TextView) v.findViewById(R.id.list_entry_summary));
			}

			v.setTag(viewHolder);
		} else {
			viewHolder = (ServiceViewHolder) v.getTag();
		}

		viewHolder.title.setText(item.title);
		if (viewHolder.description != null)
			viewHolder.description.setText(item.text);
		
		return v;
	}

	public class ServiceViewHolder {
		public final TextView title;
		public final TextView description;

		public ServiceViewHolder(TextView title, TextView description) {
			this.title = title;
			this.description = description;
		}
	}
}
