package com.cirarb.cefalog;

import java.text.ParseException;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;

public final class Utils {
	
	//This class cannot be instantiated
	private Utils() {}
	
	public static long getDateTime(Context context, String sDate, String sTime) throws ParseException {
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
		
		Date date = dateFormat.parse(sDate);
		Date time = timeFormat.parse(sTime);
		
		return date.getTime() + time.getTime();
	}

}
