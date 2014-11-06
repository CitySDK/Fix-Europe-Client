package pt.fix.europe.adapter;

import android.graphics.Bitmap;

public class IncidentRequest implements AdapterItem {
	private String title;
	private String description;
	private Bitmap bitmap;

	public IncidentRequest(String title, String description, Bitmap bitmap) {
		super();
		this.title = title;
		this.description = description;
		this.bitmap = bitmap;
	}
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
