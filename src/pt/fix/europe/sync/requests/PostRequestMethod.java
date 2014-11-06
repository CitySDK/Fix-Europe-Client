package pt.fix.europe.sync.requests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import pt.fix.europe.sync.SyncMethod;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PostRequestMethod implements SyncMethod {
	private String user;
	private String token;
	private String uri;
	private int code;
	private JsonObject entry;

	private final String TAG = this.getClass().getSimpleName();
	
	public PostRequestMethod(String user, String token, String uri) {
		this.user = user;
		this.token = token;
		this.uri = uri;
	}
	
	public void setRequest(String requestId, String serviceNotice) {
		entry = new JsonObject();
		entry.addProperty("service_request_id", requestId);
		entry.addProperty("service_request_notice", serviceNotice);
	}

	private String getB64Auth (String login, String pass) {
		   String source=login+":"+pass;
		   String ret=Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
		   return ret;
	}
	
	@Override
	public String run() {
		try {
			JsonObject body = new JsonObject();
			body.addProperty("user", user);
			body.add("request", entry);
			String postBody = new Gson().toJson(body);
			
			Log.i(TAG, postBody);
			
			URL url = new URL(uri + "rest/requests");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", getB64Auth(user, token));
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Length",
					"" + Integer.toString(postBody.getBytes().length));
			con.setUseCaches(false);

			DataOutputStream writer = new DataOutputStream(
					con.getOutputStream());
			writer.writeBytes(postBody);
			writer.flush();

			code = con.getResponseCode();
			String line, response = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			while ((line = reader.readLine()) != null) {
				response += line;
			}

			writer.close();
			reader.close();

			con.disconnect();
			return response;
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
