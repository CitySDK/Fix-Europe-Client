package pt.fix.europe.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.gson.Gson;

import pt.fix.europe.R;
import pt.fix.europe.auth.AuthPreferences;
import pt.fix.europe.auth.GoogleProfile;
import pt.fix.europe.auth.GoogleSignin;
import pt.fix.europe.auth.OnSignedInListener;
import pt.fix.europe.helper.MetadataReader;
import pt.fix.europe.listeners.OnUserProfileListener;
import pt.fix.europe.sync.OnSyncFinishedListener;
import pt.fix.europe.sync.SyncTask;
import pt.fix.europe.sync.auth.SignInMethod;
import pt.fix.europe.sync.auth.UserAuth;
import pt.fix.europe.sync.auth.UserProfile;
import pt.fix.europe.tasks.UserProfileFetcher;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SigninActivity extends SherlockActivity implements
OnSignedInListener {
	private EditText emailText;
	private EditText passwordText;

	private GoogleSignin googleSignin;
	private BasicSignin basicSignin;
	private AuthPreferences authPrefs;

	private final int SETTINGS_ID = 4;
	private final String TAG = this.getClass().getSimpleName();
	private MetadataReader reader;

	private Context context;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.signin_activity);
		context = this;
		reader = new MetadataReader(this.getApplicationContext());

		basicSignin = new BasicSignin(this);
		googleSignin = new GoogleSignin(this, this);

		emailText = (EditText) findViewById(R.id.email_text);
		passwordText = (EditText) findViewById(R.id.password_text);
		findViewById(R.id.google_button).setOnClickListener(googleSignin);
		findViewById(R.id.signin_button).setOnClickListener(basicSignin);

		authPrefs = new AuthPreferences(this);
		String type = authPrefs.getType();
		if (type == null) {
			googleSignin = new GoogleSignin(this, this);
			basicSignin = new BasicSignin(this);
		} else {
			if (type.equals("basic")) {
				basicSignin.trySignIn();
			} else {
				googleSignin.trySignIn();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addSubMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void addSubMenu(Menu menu) {
		MenuItem refreshMenu = menu.add(0, SETTINGS_ID, 0,
				R.string.menu_settings);
		refreshMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				openActivity(PreferencesActivity.class);
				return true;
			}
		});

		refreshMenu.setIcon(R.drawable.ic_menu_settings);
	}

	@SuppressWarnings("rawtypes")
	private void openActivity(Class clazz) {
		Intent mainIntent = new Intent(SigninActivity.this, clazz);
		startActivity(mainIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == GoogleSignin.AUTHORIZATION_CODE) {
				googleSignin.requestToken();
			}
		}
	}

	@Override
	public void onGoogleSignedIn(String token) {
		UserProfileFetcher task = new UserProfileFetcher(token,
				new UserProfileListener(token));
		task.execute();
	}

	private void signInFinished(int code, String json) {
		UserAuth auth = new Gson().fromJson(json, UserAuth.class);		
		authPrefs.setToken(auth.getUserToken().getAccessToken());

		Log.i(TAG, "Token set to " + authPrefs.getToken());

		openActivity(FixEuropeActivity.class);
		SigninActivity.this.finish();
	}

	class BasicSignin implements OnClickListener {
		private AuthPreferences authPreferences;
		private Activity activity;
		private ProgressDialog progressDialog;

		public BasicSignin(Activity activity) {
			this.activity = activity;
			this.authPreferences = new AuthPreferences(
					activity.getApplicationContext());

			this.progressDialog = new ProgressDialog(activity);
		}

		public void trySignIn() {
			if (authPreferences.getUser() != null
					&& authPreferences.getPassword() != null) {
				progressDialog.setTitle(activity.getResources().getString(
						R.string.signin_message));
				progressDialog.setMessage(activity.getResources().getString(
						R.string.wait));
				progressDialog.show();

				signIn(authPreferences.getUser(), authPreferences.getPassword());
			}
		}

		@Override
		public void onClick(View v) {
			String user = emailText.getText().toString();
			String password = passwordText.getText().toString();
			Dialog dialog = new Dialog(activity);
			dialog.setCancelable(true);

			if (user.equals("")) {
				dialog.setTitle(activity.getResources().getString(
						R.string.no_email));
				dialog.show();
				return;
			}

			if (password.equals("")) {
				dialog.setTitle(activity.getResources().getString(
						R.string.no_pass));
				dialog.show();
				return;
			}

			authPreferences.setUser(user);
			authPreferences.setPassword(password);
			authPreferences.setType("basic");

			signIn(user, password);
		}

		private void signIn(String user, String password) {
			progressDialog.setTitle(activity.getResources().getString(
					R.string.signin_message));
			progressDialog.setMessage(activity.getResources().getString(
					R.string.wait));
			progressDialog.show();

			UserProfile fixProfile = new UserProfile();
			fixProfile.setType("basic");
			fixProfile.setUser(user);
			fixProfile.setPassword(password);
			SyncTask task = new SyncTask(new SignInMethod(fixProfile,
					reader.getFixEuropeUri(), context), new OnSyncFinishedListener() {

				@Override
				public void onSyncFinished(int code, String result) {
					if (code == 200) {
						signInFinished(code, result);
						progressDialog.dismiss();
					} else {
						Dialog dialog = new Dialog(activity);
						dialog.setCancelable(true);
						dialog.setTitle(result);
						progressDialog.dismiss();
					}
				}
			});
			task.execute();
		}
	}

	class UserProfileListener implements OnUserProfileListener {
		private String token;

		public UserProfileListener(String token) {
			this.token = token;
		}

		@Override
		public void onUserProfileFetched(GoogleProfile profile) {

			if (profile != null) {
				UserProfile fixProfile = new UserProfile();
				fixProfile.setType("google");

				authPrefs = new AuthPreferences(getApplicationContext());				
				//authPrefs.setUser(profile.getId());
				authPrefs.setUser(profile.getEmail());
				//fixProfile.setUser(authPrefs.getUser());

				fixProfile.setUser(profile.getId());
				fixProfile.setEmail(profile.getEmail());
				fixProfile.setToken(token);

				Log.i(TAG, "Authenticating with Fix Europe");
				SyncTask task = new SyncTask(new SignInMethod(fixProfile,
						reader.getFixEuropeUri(), context),
						new OnSyncFinishedListener() {

					@Override
					public void onSyncFinished(int code, String result) {
						if (code == 200) {
							signInFinished(code, result);
							googleSignin.dismissProgress();
						} else {
							googleSignin.requestToken();
						}
					}
				});
				task.execute();
			} else {
				googleSignin.requestToken();
			}

		}
	}
}
