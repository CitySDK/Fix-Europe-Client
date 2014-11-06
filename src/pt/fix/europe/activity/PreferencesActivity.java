package pt.fix.europe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pt.fix.europe.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Patterns;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class PreferencesActivity extends SherlockPreferenceActivity {
	protected boolean reloadApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		String[] emails = readEmails();
		ListPreference preference = (ListPreference) findPreference("prefered_email");
		preference.setEntries(emails);
		preference.setEntryValues(emails);
	}

	private String[] readEmails() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; 
		Account[] accounts = AccountManager.get(this).getAccounts();
		List<String> emails = new ArrayList<String>();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        String possibleEmail = account.name;
		        if(!emails.contains(possibleEmail))
		        	emails.add(possibleEmail);
		        
		        System.out.println(possibleEmail);
		    }
		}
		
		return emails.toArray(new String[emails.size()]);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
