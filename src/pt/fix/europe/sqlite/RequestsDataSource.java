package pt.fix.europe.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RequestsDataSource {
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_REQUEST_ID,
			SQLiteHelper.COLUMN_REQUEST_DESCRIPTION,
			SQLiteHelper.COLUMN_REQUEST_DATIME,
			SQLiteHelper.COLUMN_REQUEST_SYNCHED };

	public RequestsDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createIncident(String requestId, String requestDescription,
			String requestDateTime, boolean synched) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_REQUEST_ID, requestId);
		values.put(SQLiteHelper.COLUMN_REQUEST_DESCRIPTION, requestDescription);
		values.put(SQLiteHelper.COLUMN_REQUEST_DATIME, requestDateTime);
		values.put(SQLiteHelper.COLUMN_REQUEST_SYNCHED, synched);

		database.insert(SQLiteHelper.TABLE_REQUESTS, null, values);
		Log.i(RequestsDataSource.class.getName(), "Incident created with id "
				+ requestId);
	}

	public void deleteIncident(ReportedIncident incident) {
		String id = incident.serviceRequestId;
		Log.i(RequestsDataSource.class.getName(), "Incident deleted with id: "
				+ id);
		database.delete(SQLiteHelper.TABLE_REQUESTS,
				SQLiteHelper.COLUMN_REQUEST_ID + " = " + id, null);
	}

	public List<ReportedIncident> getAllIncidents() {
		List<ReportedIncident> responses = new ArrayList<ReportedIncident>();

		Cursor cursor = database.query(SQLiteHelper.TABLE_REQUESTS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ReportedIncident response = cursorToIncident(cursor);
			responses.add(response);
			cursor.moveToNext();
		}

		cursor.close();
		return responses;
	}
	
	public List<ReportedIncident> getAllUnsynched() {
		List<ReportedIncident> responses = new ArrayList<ReportedIncident>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_REQUESTS, allColumns,
				SQLiteHelper.COLUMN_REQUEST_SYNCHED + "=0", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ReportedIncident response = cursorToIncident(cursor);
			responses.add(response);
			cursor.moveToNext();
		}

		cursor.close();
		return responses;
	}

	private ReportedIncident cursorToIncident(Cursor cursor) {
		ReportedIncident response = new ReportedIncident();
		response.serviceRequestId = cursor.getString(0);
		response.serviceRequestDescription = cursor.getString(1);
		response.serviceRequestDatetime = cursor.getString(2);
		response.serviceRequestSynched = cursor.getInt(3) == 0 ? false : true;
		return response;
	}

	public void deleteAllIncidents() {
		database.delete(SQLiteHelper.TABLE_REQUESTS, null, null);
	}

	public void requestSynched(String requestId) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_REQUEST_SYNCHED, true);

		database.update(SQLiteHelper.TABLE_REQUESTS, values,
				SQLiteHelper.COLUMN_REQUEST_ID + "=" + requestId, null);
	}
}
