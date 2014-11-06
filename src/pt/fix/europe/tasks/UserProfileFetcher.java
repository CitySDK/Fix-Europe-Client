package pt.fix.europe.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import pt.fix.europe.auth.GoogleProfile;
import pt.fix.europe.listeners.OnUserProfileListener;

import android.os.AsyncTask;

public class UserProfileFetcher extends AsyncTask<Void, Void, GoogleProfile> {
	private final String uri = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";
	private String token;
	private OnUserProfileListener listener;

	public UserProfileFetcher(String token, OnUserProfileListener listener) {
		this.token = token;
		this.listener = listener;
	}

	@Override
	protected GoogleProfile doInBackground(Void... params) {
		try {
			URL url = new URL(uri + token);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			return new Gson().fromJson(readStream(con.getInputStream()), GoogleProfile.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(GoogleProfile result) {
		listener.onUserProfileFetched(result);
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			String response = "";
			while ((line = reader.readLine()) != null) {
				response += line;
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return token;
	}
}
