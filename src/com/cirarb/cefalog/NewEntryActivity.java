package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.Calendar;

import com.cirarb.cefalog.fragments.DatePickerFragment;
import com.cirarb.cefalog.fragments.TimePickerFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.annotation.TargetApi;
import android.os.Build;

public class NewEntryActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_entry);

		setupActionBar();
		setupDateTimeButtons();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setupDateTimeButtons(){
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(getApplicationContext());
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(getApplicationContext());
		
		Calendar c = Calendar.getInstance();
		
		Button btnDate = (Button) findViewById(R.id.btnDate);
		btnDate.setText(dateFormat.format(c.getTime()));
		btnDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
					showDatePickerDialog(R.id.btnDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		Button btnTime = (Button) findViewById(R.id.btnTime);
		btnTime.setText(timeFormat.format(c.getTime()));
		btnTime.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
            		showTimePickerDialog(R.id.btnTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}

	public void showDatePickerDialog(int button) throws ParseException {
		Bundle args = new Bundle();
        args.putInt("button", button);
        
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.setArguments(args);
		dpf.show(getSupportFragmentManager(), "datePicker");
	}
	
	public void showTimePickerDialog(int button) throws ParseException {
		Bundle args = new Bundle();
        args.putInt("button", button);
        
        TimePickerFragment dpf = new TimePickerFragment();
        dpf.setArguments(args);
		dpf.show(getSupportFragmentManager(), "timePicker");
	}
}
