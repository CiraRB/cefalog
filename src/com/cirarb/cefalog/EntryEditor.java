package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.Calendar;

import com.cirarb.cefalog.fragments.DatePickerFragment;
import com.cirarb.cefalog.fragments.TimePickerFragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class EntryEditor extends FragmentActivity {
	private static final String TAG = "EntryEditor";
	
	// The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;
	
	private Uri mUri;
	private int mState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
        final String action = intent.getAction();		
		if (Intent.ACTION_INSERT.equals(action)) {
            // Requested to insert: set that state, and create a new entry in the container.
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(intent.getData(), null);

            if (mUri == null) {
                Log.e(TAG, "Failed to insert new entry into " + getIntent().getData());
                finish();
                return;
            }

            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else {
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }
		
		setContentView(R.layout.activity_entry_editor);
		
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
		getMenuInflater().inflate(R.menu.entry_editor, menu);
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
