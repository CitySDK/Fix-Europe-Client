package pt.fix.europe.sync;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SyncPreferences {
	private static final String LAST_SYNC = "last_sync";
	
	private SharedPreferences preferences;
	
	public SyncPreferences(Context context) {
		preferences = context
				.getSharedPreferences("auth", Context.MODE_PRIVATE);
	}
	
	public DateTime getLastSync() {
		String lastSync = preferences.getString(LAST_SYNC, null);
		if (lastSync != null)
			return new DateTime(lastSync);
		
		return null;
	}
	
	public void setLastSync(DateTime time) {
		Editor editor = preferences.edit();
		editor.putString(LAST_SYNC, time.toString());
		editor.commit();
	}
}
