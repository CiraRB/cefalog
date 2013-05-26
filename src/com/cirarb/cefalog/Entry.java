package com.cirarb.cefalog;

import android.content.Context;

public class Entry {
	
	private static Integer[] icIntensity =  {
		R.drawable.ic_yellow_circle,
		R.drawable.ic_orange_circle,
		R.drawable.ic_red_circle
	};
	
	public static int getIntensityIcon (Context context, int intensity) {
		int max = context.getResources().getInteger(R.integer.MaxIntensity) + 1;
		int icon = intensity * icIntensity.length / max;
		return icIntensity[icon];
	}
}
