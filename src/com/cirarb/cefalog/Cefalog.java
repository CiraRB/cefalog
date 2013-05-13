package com.cirarb.cefalog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Cefalog extends Activity {

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

}
