package pt.fix.europe.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PhotoContainer {
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri fileUri;
	private String filename;
	
	public PhotoContainer() {
		filename = "";
	}
	
	public String getFilename() {
		return filename;
	}
	
	public Uri getOutputMediaFileUri(int type) {
		if (fileUri == null)
			fileUri = Uri.fromFile(getOutputMediaFile(type));
		
		return fileUri;
	}

	private File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"reports");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.i("Reports", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			filename = "IMG_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ filename);
		} else {
			return null;
		}

		Log.i("Reports", "Created " + mediaFile.getAbsolutePath());
		return mediaFile;
	}
	
	public String getBytes(Uri fileUri) {
		File file = new File(fileUri.getPath());
	    int size = (int) file.length();
	    byte[] bytes = new byte[size];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return bytes.toString();
	}

	public void deleteFile() {
		File file = new File(fileUri.getPath());
		file.delete();
	}
}
