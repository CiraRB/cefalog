package com.cirarb.cefalog;

import com.cirarb.cefalog.LogDB.EntryColumns;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Cefalog extends Activity {

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
			    //startActivity(new Intent(this, EntryEditor.class));
			    startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
			    return true;
//		    case R.id.action_settings:
//			    startActivity(new Intent(this, SettingsActivity.class));
//			    return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}

}
