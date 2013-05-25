package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;

public class DateTime {
	public String date;
	public String time;
	
	DateTime(Context context) {
		setDateTime(context, Calendar.getInstance());
	}
	
	DateTime(Context context, Long date) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date + TimeZone.getDefault().getOffset(date));
		
		setDateTime(context, c);
	}
	
	DateTime(String sDate, String sTime) {
		this.date = sDate;
		this.time = sTime;
	}
	
	private void setDateTime(Context context, Calendar c) {
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
		
		this.date = dateFormat.format(c.getTime());
		this.time = timeFormat.format(c.getTime());
	}
	
	public long getTimestamp(Context context) throws ParseException {
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
		
		Date date = dateFormat.parse(this.date);
		Date time = timeFormat.parse(this.time);

		return date.getTime() + time.getTime();
	}
}
	
	

