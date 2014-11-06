package pt.fix.europe.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pt.fix.europe.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;

public class GoogleSignin implements OnClickListener,
		AccountManagerCallback<Bundle> {
	private final String scopes = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	public static final int AUTHORIZATION_CODE = 1993;
	public static final int ACCOUNT_CODE = 1601;

	private AuthPreferences authPreferences;
	private AccountManager accountManager;
	private OnSignedInListener listener;
	private Activity activity;
	private ProgressDialog progressDialog;

	public GoogleSignin(Activity activity, OnSignedInListener listener) {
		this.activity = activity;
		this.accountManager = AccountManager.get(activity
				.getApplicationContext());
		this.listener = listener;
		this.authPreferences = new AuthPreferences(
				activity.getApplicationContext());
		
		this.progressDialog = new ProgressDialog(activity);
	}

	@Override
	public void onClick(View v) {
		progressDialog.setTitle(activity.getResources().getString(R.string.signin_message));
		progressDialog.setMessage(activity.getResources().getString(R.string.wait));
		progressDialog.show();
		
		if (authPreferences.getUser() != null
				&& authPreferences.getToken() != null) {
			signInFinished();
		} else {		
			if (authPreferences.getUser() == null)
				chooseAccount();

			requestToken();
		}
	}
	
	public void trySignIn() {
		if (authPreferences.getUser() != null
				&& authPreferences.getToken() != null) {
			progressDialog.setTitle(activity.getResources().getString(R.string.signin_message));
			progressDialog.setMessage(activity.getResources().getString(R.string.wait));
			progressDialog.show();
			
			signInFinished();
		}
	}

	private void signInFinished() {
		authPreferences.setType("google");
		listener.onGoogleSignedIn(authPreferences.getToken());
	}
	
	public void dismissProgress() {
		progressDialog.dismiss();
	}

	public void requestToken() {
		Account userAccount = null;
		String user = authPreferences.getUser();
		for (Account account : accountManager.getAccountsByType("com.google")) {
			//if (account.name.equals(user)) {
				userAccount = account;
				//break;
			//}
		}

		accountManager.getAuthToken(userAccount, "oauth2:" + scopes, null,
				activity, this, null);
	}

	public void invalidateToken() {
		accountManager.invalidateAuthToken("com.google",
				authPreferences.getToken());
		authPreferences.setToken(null);
	}

	private void chooseAccount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final String[] emails = readEmails();
		builder.setTitle("Choose account").setItems(emails,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//System.out.println("Added email: "+emails[which]);
						//authPreferences.setUser(emails[which]);
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private String[] readEmails() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = accountManager.getAccounts();
		List<String> emails = new ArrayList<String>();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				if (!emails.contains(possibleEmail))
					emails.add(possibleEmail);
			}
		}

		return emails.toArray(new String[emails.size()]);
	}

	@Override
	public void run(AccountManagerFuture<Bundle> result) {
		try {
			Bundle bundle = result.getResult();

			Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
			if (launch != null) {
				activity.startActivityForResult(launch, AUTHORIZATION_CODE);
			} else {
				String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
				authPreferences.setToken(token);
				signInFinished();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
