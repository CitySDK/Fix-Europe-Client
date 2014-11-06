package pt.fix.europe.adapter;

import java.util.List;

import pt.fix.europe.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GeneralAdapter<T extends AdapterItem> extends ArrayAdapter<T> {

	public GeneralAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder viewHolder;
		AdapterItem item = getItem(position);
		
		if (v == null) {
			v = View.inflate(getContext(), R.layout.list_item, null);

			TextView title = (TextView) v.findViewById(R.id.title);
			TextView description = (TextView) v.findViewById(R.id.description);
			ImageView image = (ImageView) v.findViewById(R.id.thumbnail_image);
			ImageView pic = (ImageView) v.findViewById(R.id.picture_image);
			
			viewHolder = new ViewHolder(title, description, image, pic);
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}

		viewHolder.title.setText(item.getTitle());
		viewHolder.description.setText(item.getDescription());

		if (item.getBitmap() != null)
			viewHolder.image.setImageBitmap(item.getBitmap());
		
		/*if (item.getExtra() != null) {
			viewHolder.extra.setImageBitmap(item.getExtra());
			viewHolder.extra.setVisibility(View.VISIBLE);
		}*/
		
		v.setBackgroundResource(R.drawable.listview_selector);
		return v;
	}

	public class ViewHolder {
		public final TextView title;
		public final TextView description;
		public final ImageView image;
		public final ImageView extra;
		
		public ViewHolder(TextView title, TextView description, ImageView image, ImageView extra) {
			this.title = title;
			this.description = description;
			this.image = image;
			this.extra = extra;
		}
	}
}
