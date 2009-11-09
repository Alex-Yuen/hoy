package it.hoyland.sclottery.util;

import java.util.Calendar;

public final class CalendarUtil {

	public static String getDate() {
		String ret = "";
		int date = Calendar.getInstance().get(Calendar.DATE);
		if (date < 10) {
			ret = "0" + (new Integer(date)).toString();
		} else {
			ret = (new Integer(date)).toString();
		}
		return ret;
	}
}
