package com.cirarb.cefalog;

import java.util.HashMap;

import com.cirarb.cefalog.LogDB.EntryColumns;
import com.cirarb.cefalog.LogDB.TypeColumns;

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
    private static final int DATABASE_VERSION = 1;
    
    private static HashMap<String, String> sEntriesProjectionMap;
    private static HashMap<String, String> sTypesProjectionMap;
    
    private static final int ENTRIES = 1;
    private static final int ENTRY_ID = 2;
    private static final int TYPES = 3;
    private static final int TYPE_ID = 4;
    
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
        	sb.append(EntryColumns.DATE).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.TYPE).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.INTENSITY).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.DURATION_TIME).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.DURATION_UNIT).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.NOTES).append(" TEXT,");
        	sb.append(EntryColumns.CREATED_DATE).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.MODIFIED_DATE).append(" INTEGER NOT NULL");
        	sb.append(");");
        	
            db.execSQL(sb.toString());
            
            sb = new StringBuilder();
            sb.append("CREATE TABLE ").append(LogDB.TYPES_TABLE_NAME).append(" (");
        	sb.append(TypeColumns._ID).append(" INTEGER PRIMARY KEY,");
        	sb.append(TypeColumns.NAME).append(" TEXT NOT NULL,");
        	sb.append(EntryColumns.CREATED_DATE).append(" INTEGER NOT NULL,");
        	sb.append(EntryColumns.MODIFIED_DATE).append(" INTEGER NOT NULL");
        	sb.append(");");
        	
            db.execSQL(sb.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + LogDB.ENTRIES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LogDB.TYPES_TABLE_NAME);
            onCreate(db);
        }
        
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Downgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + LogDB.ENTRIES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LogDB.TYPES_TABLE_NAME);
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
        switch (sUriMatcher.match(uri)) {
	        case ENTRIES:
	        	qb.setTables(LogDB.ENTRIES_TABLE_NAME);
	            qb.setProjectionMap(sEntriesProjectionMap);
	            break;
	        case ENTRY_ID:
	        	qb.setTables(LogDB.ENTRIES_TABLE_NAME);
	            qb.setProjectionMap(sEntriesProjectionMap);
	            qb.appendWhere(EntryColumns._ID + "=" + uri.getPathSegments().get(1));
	            break;
	        case TYPES:
	        	qb.setTables(LogDB.TYPES_TABLE_NAME);
	            qb.setProjectionMap(sTypesProjectionMap);
	            break;
	        case TYPE_ID:
	        	qb.setTables(LogDB.TYPES_TABLE_NAME);
	            qb.setProjectionMap(sTypesProjectionMap);
	            qb.appendWhere(EntryColumns._ID + "=" + uri.getPathSegments().get(1));
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        // If no sort order is specified use the default
        String orderBy = null;
        if (TextUtils.isEmpty(sortOrder)) {
        	switch (sUriMatcher.match(uri)) {
        		case ENTRIES:
        		case ENTRY_ID:
        			orderBy = EntryColumns.DEFAULT_SORT_ORDER;
        			break;
        	}
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
	        case TYPES:
	            return TypeColumns.CONTENT_TYPE;
	        case TYPE_ID:
	            return TypeColumns.CONTENT_ITEM_TYPE;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    
    @Override
	public Uri insert(Uri uri, ContentValues initialValues) {
    	switch(sUriMatcher.match(uri)) {
    		case ENTRIES:
    			return insertEntry(initialValues);
    		case TYPES:
    			return insertType(initialValues);
    		default:
    			throw new IllegalArgumentException("Unknown/Illegal URI " + uri);
        }
	}
    
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        switch (sUriMatcher.match(uri)) {
	        case ENTRIES:
	        case ENTRY_ID:
	            return updateEntry(uri, values, where, whereArgs);
	        case TYPES:
	        case TYPE_ID:
	            return updateType(uri, values, where, whereArgs);
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        int count;
        switch (sUriMatcher.match(uri)) {
	        case ENTRIES:
	            count = db.delete(LogDB.ENTRIES_TABLE_NAME, where, whereArgs);
	            break;
	        case ENTRY_ID:
	            String entryId = uri.getPathSegments().get(1);
	            count = db.delete(LogDB.ENTRIES_TABLE_NAME, EntryColumns._ID + "=" + entryId
	                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
	
	/** Entry updates **/
	private Uri insertEntry (ContentValues initialValues) {       
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        Long now = Long.valueOf(System.currentTimeMillis());
        values.put(EntryColumns.CREATED_DATE, now);
        values.put(EntryColumns.MODIFIED_DATE, now);
        
        if (values.containsKey(EntryColumns.DATE) == false) {
        	values.put(EntryColumns.DATE, now);
        }
        if (values.containsKey(EntryColumns.NOTES) == false) {
            values.put(EntryColumns.NOTES, "");
        }
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(LogDB.ENTRIES_TABLE_NAME, EntryColumns.NOTES, values);
        if (rowId > 0) {
            Uri entryUri = ContentUris.withAppendedId(EntryColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(entryUri, null);
            return entryUri;
        }

        throw new SQLException("Failed to insert entry row");
	}
	
	private int updateEntry(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		Long now = Long.valueOf(System.currentTimeMillis());
        values.put(EntryColumns.MODIFIED_DATE, now);
        
        if (values.containsKey(EntryColumns.NOTES) == false) {
            values.put(EntryColumns.NOTES, "");
        }
		
        int count;
        switch (sUriMatcher.match(uri)) {
	        case ENTRIES:
	            count = db.update(LogDB.ENTRIES_TABLE_NAME, values, where, whereArgs);
	            break;
	        case ENTRY_ID:
	            String entryId = uri.getPathSegments().get(1);
	            count = db.update(LogDB.ENTRIES_TABLE_NAME, values, EntryColumns._ID + "=" + entryId
	                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
	
	private Uri insertType (ContentValues initialValues) {       
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        Long now = Long.valueOf(System.currentTimeMillis());
        values.put(TypeColumns.CREATED_DATE, now);
        values.put(TypeColumns.MODIFIED_DATE, now);
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(LogDB.TYPES_TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri typeUri = ContentUris.withAppendedId(TypeColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(typeUri, null);
            return typeUri;
        }

        throw new SQLException("Failed to insert type row");
	}
	
	private int updateType(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		Long now = Long.valueOf(System.currentTimeMillis());
        values.put(TypeColumns.MODIFIED_DATE, now);
        
        int count;
        switch (sUriMatcher.match(uri)) {
	        case TYPES:
	        	count = db.update(LogDB.TYPES_TABLE_NAME, values, where, whereArgs);
	            break;
	        case TYPE_ID:
	            String typeId = uri.getPathSegments().get(1);
	            count = db.update(LogDB.TYPES_TABLE_NAME, values, TypeColumns._ID + "=" + typeId
	                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}	
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.ENTRIES_TABLE_NAME, ENTRIES);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.ENTRIES_TABLE_NAME + "/#", ENTRY_ID);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.TYPES_TABLE_NAME, TYPES);
        sUriMatcher.addURI(LogDB.AUTHORITY, LogDB.TYPES_TABLE_NAME + "/#", TYPE_ID);

        sEntriesProjectionMap = new HashMap<String, String>();
        sEntriesProjectionMap.put(EntryColumns._ID, EntryColumns._ID);
        sEntriesProjectionMap.put(EntryColumns.DATE, EntryColumns.DATE);
        sEntriesProjectionMap.put(EntryColumns.TYPE, EntryColumns.TYPE);
        sEntriesProjectionMap.put(EntryColumns.INTENSITY, EntryColumns.INTENSITY);
        sEntriesProjectionMap.put(EntryColumns.DURATION_TIME, EntryColumns.DURATION_TIME);
        sEntriesProjectionMap.put(EntryColumns.DURATION_UNIT, EntryColumns.DURATION_UNIT);
        sEntriesProjectionMap.put(EntryColumns.NOTES, EntryColumns.NOTES);
        sEntriesProjectionMap.put(EntryColumns.CREATED_DATE, EntryColumns.CREATED_DATE);
        sEntriesProjectionMap.put(EntryColumns.MODIFIED_DATE, EntryColumns.MODIFIED_DATE);
        
        sTypesProjectionMap = new HashMap<String, String>();
        sTypesProjectionMap.put(TypeColumns._ID, TypeColumns._ID);
        sTypesProjectionMap.put(TypeColumns.NAME, TypeColumns.NAME);
        sTypesProjectionMap.put(TypeColumns.CREATED_DATE, TypeColumns.CREATED_DATE);
        sTypesProjectionMap.put(TypeColumns.MODIFIED_DATE, TypeColumns.MODIFIED_DATE);
    }
    
}
