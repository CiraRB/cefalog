package com.cirarb.cefalog;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class CefalogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			case R.id.action_new_entry:
			    startActivity(new Intent(this, NewEntryActivity.class));
			    return true;
//		    case R.id.action_settings:
//			    startActivity(new Intent(this, SettingsActivity.class));
//			    return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}

}
