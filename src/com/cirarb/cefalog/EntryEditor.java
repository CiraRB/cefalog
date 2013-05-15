package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.Calendar;

import com.cirarb.cefalog.LogDB.EntryColumns;
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
import android.widget.EditText;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

public class EntryEditor extends FragmentActivity {
	private static final String TAG = "EntryEditor";
	
	// The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;
	
	private Uri mUri;
	private int mState;
	private Cursor mCursor;

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
			
		case R.id.action_save:
            try {
				saveEntry();
				finish();
			} catch (ParseException e) {
				Log.e(TAG, "ParseException: " + e.getMessage());
	            finish();
			}
            break;
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
	
	private final void saveEntry() throws ParseException {
        if (mCursor != null) {
            // Get out updates into the provider.
            ContentValues values = new ContentValues();

            // Bump the modification time to now.
            values.put(EntryColumns.MODIFIED_DATE, System.currentTimeMillis());

            // Write our text back into the provider.
            EditText etNotes = (EditText)findViewById(R.id.etNotes);
            Button btnDate = (Button)findViewById(R.id.btnDate);
            Button btnTime = (Button)findViewById(R.id.btnTime);
            
            values.put(EntryColumns.DATE, Utils.getDateTime(getApplicationContext(), 
            		btnDate.getText().toString(), btnTime.getText().toString()));
            values.put(EntryColumns.NOTES, etNotes.getText().toString());

            // Commit all of our changes to persistent storage. When the update completes
            // the content provider will notify the cursor of the change, which will
            // cause the UI to be updated.
            try {
                getContentResolver().update(mUri, values, null, null);
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
            
        }
    }
}
