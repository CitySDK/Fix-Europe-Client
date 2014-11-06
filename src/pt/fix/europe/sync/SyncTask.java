package pt.fix.europe.sync;

import android.os.AsyncTask;

public class SyncTask extends AsyncTask<Void, Void, String> {
	private OnSyncFinishedListener listener;
	private SyncMethod method;
	
	public SyncTask(SyncMethod method, OnSyncFinishedListener listener) {
		this.listener = listener;
		this.method = method;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		return method.run();
	}

	@Override
	protected void onPostExecute(String result) {
		listener.onSyncFinished(method.getCode(), result);
	}
}
