package pt.fix.europe.sync.auth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import pt.fix.europe.sync.SyncMethod;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class SignInMethod implements SyncMethod {
	private String uri;
	private UserProfile profile;
	private final String TAG = this.getClass().getSimpleName();
	private int responseCode;
	private Context context;
	
	public SignInMethod(UserProfile profile, String uri, Context context) {
		this.uri = uri;
		this.profile = profile;
		this.context = context;
	}
	
	private static void prepareSecurity() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				// System.out.println("Warning: URL Host: "+urlHostName+" vs. "+session.getPeerHost());
				return true;
			}
		};

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {

		}
	}
	
	@Override
	public String run() {
		try {
			String user = new Gson().toJson(profile, UserProfile.class);
			Log.i(TAG, "Authenticating user " + user);
			
			prepareSecurity();
			
			URL url = new URL(uri + "rest/auth");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Length", "" + Integer.toString(user.getBytes().length));
			con.setUseCaches (false);

			DataOutputStream writer = new DataOutputStream(con.getOutputStream());
			writer.writeBytes(user);
			writer.flush();
			
			responseCode = con.getResponseCode();
			String line, response = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			while ((line = reader.readLine()) != null) {
			    response += line;
			}
			
			writer.close();
			reader.close(); 
			
			con.disconnect();
			return response;
		} catch (FileNotFoundException e) {
			startNotification();
			e.printStackTrace();
		} catch (MalformedURLException e) {
			startNotification();
			e.printStackTrace();
		} catch (ProtocolException e) {
			startNotification();
			e.printStackTrace();
		} catch (IOException e) {
			startNotification();
			e.printStackTrace();
		}		
		return null;
	}

	@Override
	public int getCode() {
		return responseCode;
	}
	
	private void startNotification() {
		((Activity) context).runOnUiThread(new Runnable() {
		    public void run() {
				Toast.makeText(context, "Problem with the authentication", Toast.LENGTH_LONG).show();
		    }
		});
	}
}
