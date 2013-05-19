package com.cirarb.cefalog.fragments;

import com.cirarb.cefalog.LogDB.EntryColumns;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class EntriesFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor> {
    
    SimpleCursorAdapter mAdapter;
      
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        setEmptyText("No data");
        
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null,
                new String[] { EntryColumns.DATE, EntryColumns.NOTES }, 
                new int[] { android.R.id.text1, android.R.id.text2 }, 0);
        setListAdapter(mAdapter);
        
        setListShown(false);
        
        getLoaderManager().initLoader(0, null, this);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), EntryColumns.CONTENT_URI,
                EntryColumns.PROJECTION, null, null, EntryColumns.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
}
