package com.cirarb.cefalog;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for LogDBProvider
 */
public final class LogDB {
	public static final String AUTHORITY = "com.cirarb.cefalog.provider.LogDB";
	
	public static final String ENTRIES_TABLE_NAME = "entries";
	public static final String TYPES_TABLE_NAME = "types";
	
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
         * The time stamp for the date log entry
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String DATE = "date";
        /**
         * The type of headache identifier
         * <P>Type: INTEGER</P>
         */
        public static final String TYPE = "type";
        /**
         * The intensity of headache
         * <P>Type: INTEGER</P>
         */
        public static final String INTENSITY = "intensity";
        /**
         * The time duration of headache
         * <P>Type: INTEGER</P>
         */
        public static final String DURATION_TIME = "duration_time";
        /**
         * The unit duration of headache
         * <P>Type: INTEGER</P>
         */
        public static final String DURATION_UNIT = "duration_unit";
        /**
         * The notes for the log entry
         * <P>Type: TEXT</P>
         */
        public static final String NOTES = "notes";
        /**
         * The time stamp for when the note was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";
        /**
         * The time stamp for when the note was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
        
        public static final String[] PROJECTION = new String[] 
        		{ _ID, DATE, TYPE, INTENSITY, DURATION_TIME, DURATION_UNIT, NOTES  };
    }
    
    public static final class TypeColumns implements BaseColumns {
        
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TYPES_TABLE_NAME);
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of types of headaches.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cefalog.type";
        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single type of headache.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cefalog.type";
        /**
         * The name of the type of headache
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";
        /**
         * The time stamp for when the note was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";
        /**
         * The time stamp for when the note was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
        
        public static final String[] PROJECTION = new String[] { _ID, NAME };
    }
}
