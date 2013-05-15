package com.cirarb.cefalog.fragments;

import java.text.ParseException;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

	Button btn;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		Bundle args = getArguments();
		int id = args == null ? 0 : args.getInt("button", 0);
		
		btn = (Button)getActivity().findViewById(id);
		String time = btn.getText().toString();
		if (time.length() > 0) {
			java.text.DateFormat timeFormat = DateFormat.getTimeFormat(getActivity().getApplicationContext());
			try {
				c.setTime(timeFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(getActivity().getApplicationContext());
		btn.setText(timeFormat.format(c.getTime()));
	}
}
