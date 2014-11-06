package pt.fix.europe.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class MetadataReader {
	private static final String DEBUG = "pt.open311.debug";
	private static final String KEY = "pt.open311.key";
	private static final String FIX_EUROPE = "pt.fix.europe.uri";

//	private static final String DEBUG = "lm.open311.debug";
//	private static final String KEY = "lm.open311.key";
//	private static final String FIX_EUROPE = "pt.fix.europe.uri";
	
	private PackageManager manager;
	private String packageName;
	
	public MetadataReader(Context ctx) {
		this.manager = ctx.getPackageManager();
		this.packageName = ctx.getPackageName();
	}
	
	public String getCitySDKUri() {
		return getMetadata(DEBUG);
	}

	public String getCitySDKKey() {
		return getMetadata(KEY).substring(1);
	}
	
	public String getFixEuropeUri() {
		return getMetadata(FIX_EUROPE);
	}
	
	private String getMetadata(String name) {
		try {
			ApplicationInfo ai = manager.getApplicationInfo(
					packageName, PackageManager.GET_META_DATA);

			return ai.metaData.getString(name);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
