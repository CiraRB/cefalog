package com.cirarb.cefalog.fragments;

import java.util.ArrayList;
import java.util.List;

import com.cirarb.cefalog.DateTime;
import com.cirarb.cefalog.Entry;
import com.cirarb.cefalog.LogDB;
import com.cirarb.cefalog.LogDB.EntryColumns;
import com.cirarb.cefalog.LogDB.TypeColumns;
import com.cirarb.cefalog.R;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EntriesFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor> {
    
    SimpleCursorAdapter mAdapter;
    List<String> mTypes;
      
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.text_empty_list));
        //setTypeList();
        setEntryAdapter();
        
        getLoaderManager().initLoader(0, null, this);
    }
       
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Uri entryUri = ContentUris.withAppendedId(getActivity().getIntent().getData(), id);
		startActivity(new Intent(Intent.ACTION_EDIT, entryUri));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), EntryColumns.CONTENT_URI,
                EntryColumns.PROJECTION, null, null, EntryColumns.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		setTypeList();
		mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	private void setEntryAdapter() {
		 mAdapter = new SimpleCursorAdapter(getActivity(),
	                R.layout.item_list_entry, null,
	                new String[] { EntryColumns.INTENSITY, EntryColumns.DATE, EntryColumns.TYPE }, 
	                new int[] { R.id.ivIntensity, R.id.tvDate, R.id.tvType }, 0);
		mAdapter.setViewBinder(new EntryViewBinder());
		setListAdapter(mAdapter);
		setListShown(false);
	}
	
	public void setTypeList() {
		mTypes = new ArrayList<String>();
        Cursor cursor = getActivity().getContentResolver().query(
        		LogDB.TypeColumns.CONTENT_URI, TypeColumns.PROJECTION, null, null, null);
        if (cursor.moveToFirst()) {
            do {
            	mTypes.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
	}

	private class EntryViewBinder implements ViewBinder {
		 
	    @Override
	    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	        if (columnIndex == cursor.getColumnIndex(EntryColumns.INTENSITY)) {
	        	int intensity = cursor.getInt(columnIndex);
	        	ImageView iv = (ImageView)view.findViewById(R.id.ivIntensity);
	        	iv.setImageResource(Entry.getIntensityIcon(getActivity().getApplicationContext(), intensity));
	            return true;
	            
	        } else if (columnIndex == cursor.getColumnIndex(EntryColumns.DATE)) {
	        	long date = cursor.getLong(columnIndex);
	        	TextView tv = (TextView)view.findViewById(R.id.tvDate);
	        	DateTime dt = new DateTime(getActivity().getApplicationContext(), date);
	        	tv.setText(dt.date);
	        	return true;
	        }
	        
	        else if (columnIndex == cursor.getColumnIndex(EntryColumns.TYPE)) {
	        	int type = cursor.getInt(columnIndex);
	        	TextView tv = (TextView)view.findViewById(R.id.tvType);
	        	String str = mTypes.get(type);
	        	tv.setText(str);
	        	return true;
	        }

	        return false;
	    }
	 
	}
}
