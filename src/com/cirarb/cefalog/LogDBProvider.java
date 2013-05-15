package com.cirarb.cefalog;

import java.util.HashMap;

import com.cirarb.cefalog.LogDB.EntryColumns;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of log entries. Each entry has date and notes.
 */
public class LogDBProvider extends ContentProvider {
	
	private static final String TAG = "LogDBProvider";
	
	private static final String DATABASE_NAME = "cefalog.db";
    private static final int DATABASE_VERSION = 2;
    
    private static HashMap<String, String> sEntriesProjectionMap;
    
    private static final int ENTRIES = 1;
    private static final int ENTRY_ID = 2;
    
    private static final UriMatcher sUriMatcher;
    
	/**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	StringBuilder sb = new StringBuilder();
        	sb.append("CREATE TABLE ").append(LogDB.ENTRIES_TABLE_NAME).append(" (");
        	sb.append(EntryColumns._ID).append(" INTEGER PRIMARY KEY,");
        	sb.append(EntryColumns.DATE).append(" INTEGER,");
        	sb.append(EntryColumns.NOTES).append(" TEXT");
        	sb.append(");");
        	
            db.execSQL(sb.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + LogDB.ENTRIES_TABLE_NAME);
            onCreate(db);
        }
    }
    
    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    
    @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LogDB.ENTRIES_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case ENTRIES:
            qb.setProjectionMap(sEntriesProjectionMap);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = EntryColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}
    
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case ENTRIES:
            return EntryColumns.CONTENT_TYPE;

        case ENTRY_ID:
            return EntryColumns.CONTENT_ITEM_TYPE;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    @Override
	public Uri insert(Uri uri, ContentValues initialValues) {
    	// Validate the requested uri
        if (sUriMatcher.match(uri) != ENTRIES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        if (values.containsKey(EntryColumns.NOTES) == false) {
            values.put(EntryColumns.NOTES, "");
        }
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(LogDB.ENTRIES_TABLE_NAME, EntryColumns.NOTES, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(EntryColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.ENTRIES_TABLE_NAME, ENTRIES);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.ENTRIES_TABLE_NAME + "/#", ENTRY_ID);

        sEntriesProjectionMap = new HashMap<String, String>();
        sEntriesProjectionMap.put(EntryColumns._ID, EntryColumns._ID);
        sEntriesProjectionMap.put(EntryColumns.DATE, EntryColumns.DATE);
        sEntriesProjectionMap.put(EntryColumns.NOTES, EntryColumns.NOTES);
    }
    
    /*--------------------------------------*/

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
    
}
