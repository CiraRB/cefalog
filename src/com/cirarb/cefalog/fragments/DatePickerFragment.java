package com.cirarb.cefalog.fragments;

import java.text.ParseException;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
	
	Button btn;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		Bundle args = getArguments();
		int id = args == null ? 0 : args.getInt("button", 0);
		
		btn = (Button)getActivity().findViewById(id);
		String date = btn.getText().toString();
		if (date.length() > 0) {
			java.text.DateFormat dateFormat = DateFormat.getDateFormat(getActivity().getApplicationContext());
			try {
				c.setTime(dateFormat.parse(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(getActivity().getApplicationContext());
		btn.setText(dateFormat.format(c.getTime()));
	}
}
