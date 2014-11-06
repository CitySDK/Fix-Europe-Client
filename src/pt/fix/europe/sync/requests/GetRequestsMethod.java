package pt.fix.europe.sync.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.util.Base64;
import android.util.Log;

import pt.fix.europe.sync.SyncMethod;

public class GetRequestsMethod implements SyncMethod {
	private int code;
	private String user;
	private String uri;
	private String token;
	private final String TAG = this.getClass().getSimpleName();
	
	public GetRequestsMethod(String user, String token, String uri) {
		this.user = user;
		this.token = token;
		this.uri = uri;
	}
	
	private String getB64Auth (String login, String pass) {
		   String source=login+":"+pass;
		   String ret=Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
		   return ret;
	}
	
	@Override
	public String run() {
		try {
			Log.i(TAG, "Synching requests of user " + user);
			
			URL url = new URL(uri + "rest/requests?user=" + user + "&limit=-1");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization",getB64Auth(user, token));
			con.setRequestProperty("Accept", "application/json");
			
			code = con.getResponseCode();
			if (code == 200) {
				String line, response = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
				while ((line = reader.readLine()) != null) {
				    response += line;
				}
				
				reader.close(); 
				con.disconnect();
				
				return response;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int getCode() {
		return code;
	}
	
}
