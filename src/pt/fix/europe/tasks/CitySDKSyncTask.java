package pt.fix.europe.tasks;

import open311.requests.Parameter;
import pt.fix.europe.listeners.OnDataListener;
import android.os.AsyncTask;

public class CitySDKSyncTask extends AsyncTask<Parameter, Void, Object> {
	private Fetcher fetcher;
	private OnDataListener listener;
	
	public CitySDKSyncTask(Fetcher fetcher, OnDataListener listener) {
		this.fetcher = fetcher;
		this.listener = listener;
	}
	
	@Override
	protected Object doInBackground(Parameter... params) {
		return fetcher.fetchData(params);
	}

	@Override
	protected void onPostExecute(Object result) {
		listener.onDataReceived(result);
	}
}
