package com.cirarb.cefalog;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for LogDBProvider
 */
public final class LogDB {
	public static final String AUTHORITY = "com.cirarb.cefalog.provider.LogDB";
	
	public static final String ENTRIES_TABLE_NAME = "entries";
	
	//This class cannot be instantiated
	private LogDB() {}
	
    public static final class EntryColumns implements BaseColumns {
    	
        public static final String DEFAULT_SORT_ORDER = "date DESC";
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ENTRIES_TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of entries.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cefalog.entry";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cefalog.entry";
        
        /**
         * The timestamp for the date log entry
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String DATE = "date";
        /**
         * The notes for the log entry
         * <P>Type: TEXT</P>
         */
        public static final String NOTES = "notes";
        /**
         * The timestamp for when the note was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the note was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
        
        public static final String[] PROJECTION = new String[] {
        	EntryColumns._ID,
            EntryColumns.DATE,
            EntryColumns.NOTES
        };
    }
}
