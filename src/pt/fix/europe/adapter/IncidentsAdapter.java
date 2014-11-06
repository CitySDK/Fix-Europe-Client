package pt.fix.europe.adapter;

import java.util.List;

import android.content.Context;

public class IncidentsAdapter extends GeneralAdapter<IncidentRequest> {

	public IncidentsAdapter(Context context, int textViewResourceId,
			List<IncidentRequest> objects) {
		super(context, textViewResourceId, objects);
	}
}
