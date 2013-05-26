package com.cirarb.cefalog;

import java.util.ArrayList;
import java.util.List;

import com.cirarb.cefalog.LogDB.EntryColumns;
import com.cirarb.cefalog.fragments.EntriesFragment;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class Cefalog extends FragmentActivity {

	CefalogAdapter pageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If no data was given in the intent (because we were started
        // as a MAIN activity), then use our default content provider.
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(EntryColumns.CONTENT_URI);
        }
		
		setContentView(R.layout.activity_cefalog);
		
		List<Fragment> fragments = getFragments();
		pageAdapter = new CefalogAdapter(getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
		pager.setAdapter(pageAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cefalog, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_entry:
			    startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
			    return true;
//		    case R.id.action_settings:
//			    startActivity(new Intent(this, SettingsActivity.class));
//			    return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}
	
	private List<Fragment> getFragments(){
		  List<Fragment> fList = new ArrayList<Fragment>();
		  
		  fList.add(new EntriesFragment());
		 
		  return fList;
	}

}
