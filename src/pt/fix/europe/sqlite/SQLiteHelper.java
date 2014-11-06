package pt.fix.europe.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_REQUESTS = "requests";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_REQUEST_ID = "service_request_id";
	public static final String COLUMN_REQUEST_DATIME = "service_request_datetime";
	public static final String COLUMN_REQUEST_DESCRIPTION = "service_request_description";
	public static final String COLUMN_REQUEST_SYNCHED = "request_synched";

	private static final String DATABASE_NAME = "request.db";
	private static final int DATABASE_VERSION = 10;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_REQUESTS + " ("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_REQUEST_ID + " text not null, "
			+ COLUMN_REQUEST_DATIME + " text, " 
			+ COLUMN_REQUEST_DESCRIPTION + " text, " 
			+ COLUMN_REQUEST_SYNCHED + " integer);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(SQLiteHelper.class.getName(), DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
		onCreate(db);
	}
}