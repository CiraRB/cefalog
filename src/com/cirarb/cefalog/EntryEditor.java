package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import com.cirarb.cefalog.LogDB.EntryColumns;
import com.cirarb.cefalog.LogDB.TypeColumns;
import com.cirarb.cefalog.fragments.AddDialogFragment;
import com.cirarb.cefalog.fragments.DatePickerFragment;
import com.cirarb.cefalog.fragments.TimePickerFragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

public class EntryEditor extends FragmentActivity
	implements AddDialogFragment.AddDialogListener {
	private static final String TAG = "EntryEditor";
	
	ArrayAdapter<String> aaTypes;
	Cursor cEdit;
	Boolean bEdit = false;
	
    Button btnDate;
    Button btnTime;
	Spinner spnType;
	SeekBar sbIntensity;
	TextView tvIntensity;
	EditText etDuration;
	Spinner spnDuration;
	EditText etNotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_entry_editor);
		
		etNotes = (EditText)findViewById(R.id.etNotes);
        btnDate = (Button)findViewById(R.id.btnDate);
        btnTime = (Button)findViewById(R.id.btnTime);
        spnType = (Spinner) findViewById(R.id.spnType);
        sbIntensity = (SeekBar) findViewById(R.id.sbIntensity);
        tvIntensity = (TextView) findViewById(R.id.tvIntensity);
        etDuration = (EditText)findViewById(R.id.etDuration);
        spnDuration = (Spinner) findViewById(R.id.spnDuration);        
        
        if (Intent.ACTION_EDIT.equals(getIntent().getAction())) {
        	Uri uri = getIntent().getData();
        	cEdit = getContentResolver().query(uri, EntryColumns.PROJECTION, null, null, null);
        	if (cEdit.getCount() > 0) {
        		cEdit.moveToFirst();
        		bEdit = true;
        	}
        }

		setupActionBar();
		setValues();
		bindEvents();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry_editor, menu);
		
		menu.findItem(R.id.action_delete).setVisible(
				Intent.ACTION_EDIT.equals(getIntent().getAction()));
		
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
		
		case R.id.action_delete:
	        try {
				deleteEntry();
				finish();
			} catch (ParseException e) {
				Log.e(TAG, "ParseException: " + e.getMessage());
	            finish();
			}
	        break;
		}
	
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) { 
		EditText etValue = (EditText) dialog.getDialog().findViewById(R.id.etValue);
		String value = etValue.getText().toString();
		
		if (value.length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.error_value_empty), Toast.LENGTH_SHORT).show();
			showAddTypeDialog();
			
		} else {
			ContentValues values = new ContentValues();
			values.put(TypeColumns.NAME, value);
			Uri uri = getContentResolver().insert(TypeColumns.CONTENT_URI, values);
			if (uri != null) {
				aaTypes.insert(value, aaTypes.getCount() - 1);
			}
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) { 
		if (aaTypes.getCount() > 1) {
			spnType.setSelection(0);
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	private void setValues() {
		List<String> types = new ArrayList<String>();
        Cursor cursor = getContentResolver().query(
        		LogDB.TypeColumns.CONTENT_URI, TypeColumns.PROJECTION, null, null, null);
        if (cursor.moveToFirst()) {
            do {
            	types.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        types.add(getString(R.string.text_other));
 
        aaTypes = new ArrayAdapter<String>(
        		this, android.R.layout.simple_spinner_item, types);
        aaTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(aaTypes);
        
		DateTime dt;
		
		if (bEdit) {
			dt = new DateTime(getApplicationContext(), 
					cEdit.getLong(cEdit.getColumnIndex(EntryColumns.DATE)));
        	spnType.setSelection(
        			cEdit.getInt(cEdit.getColumnIndex(EntryColumns.TYPE)));
			sbIntensity.setProgress(
					cEdit.getInt(cEdit.getColumnIndex(EntryColumns.INTENSITY)));
			etDuration.setText(
					cEdit.getString(cEdit.getColumnIndex(EntryColumns.DURATION_TIME)));
			spnDuration.setSelection(
        			cEdit.getInt(cEdit.getColumnIndex(EntryColumns.DURATION_UNIT)));
			etNotes.setText(
					cEdit.getString(cEdit.getColumnIndex(EntryColumns.NOTES)));
		} else 
			dt = new DateTime(getApplicationContext());
		
		btnDate.setText(dt.date);
		btnTime.setText(dt.time);
		
		tvIntensity.setText(String.valueOf(sbIntensity.getProgress() + 1));
	}
	
	private void bindEvents(){
		btnDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
					showDatePickerDialog(R.id.btnDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
            }
        });
		
		btnTime.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
            		showTimePickerDialog(R.id.btnTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
            }
        });
		
		spnType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            	if (position == spnType.getCount() - 1) {
            		showAddTypeDialog();
            	}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
		
		sbIntensity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            	tvIntensity.setText(String.valueOf(progress + 1));
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) { }
 
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
	}

	private void showDatePickerDialog(int button) throws ParseException {
		Bundle args = new Bundle();
        args.putInt("button", button);
        
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.setArguments(args);
		dpf.show(getSupportFragmentManager(), "datePicker");
	}
	
	private void showTimePickerDialog(int button) throws ParseException {
		Bundle args = new Bundle();
        args.putInt("button", button);
        
        TimePickerFragment dpf = new TimePickerFragment();
        dpf.setArguments(args);
		dpf.show(getSupportFragmentManager(), "timePicker");
	}
	
	private void showAddTypeDialog() {
		Bundle args = new Bundle();
		args.putInt("title", R.string.action_add_type);
		
		AddDialogFragment df = new AddDialogFragment();
		df.setArguments(args);
		df.show(getSupportFragmentManager(), "AddDialogFragment");
	}
	
	private final void saveEntry() throws ParseException {
        ContentValues values = new ContentValues();
        
        DateTime dt = new DateTime(btnDate.getText().toString(),
        		btnTime.getText().toString());
        
        values.put(EntryColumns.DATE, dt.getTimestamp(getApplicationContext()));
        values.put(EntryColumns.TYPE, spnType.getSelectedItemId());
        values.put(EntryColumns.INTENSITY, sbIntensity.getProgress());
        values.put(EntryColumns.DURATION_TIME, etDuration.getText().toString());
        values.put(EntryColumns.DURATION_UNIT, spnDuration.getSelectedItemId());
        values.put(EntryColumns.NOTES, etNotes.getText().toString());
        
        try {
        	final Intent intent = getIntent();
            final String action = intent.getAction();		
            
            if (Intent.ACTION_EDIT.equals(action)) {
            	getContentResolver().update(intent.getData(), values, null, null);
            	
            } else if (Intent.ACTION_INSERT.equals(action)) {
            	Uri uri = getContentResolver().insert(intent.getData(), values);
            	Toast.makeText(getApplicationContext(), getString(R.string.text_saved), Toast.LENGTH_SHORT).show();
            	setResult(RESULT_OK, (new Intent()).setAction(uri.toString()));
            }
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }
	
	private final void deleteEntry() throws ParseException {       
        try {
        	getContentResolver().delete(getIntent().getData(), null, null);
        	Toast.makeText(getApplicationContext(), getString(R.string.text_deleted), Toast.LENGTH_SHORT).show();
        	finish();

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
