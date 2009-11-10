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

	public static String getMonthAndDate() {
		String ret = "";
		Calendar cal = Calendar.getInstance();

		int month = cal.get(Calendar.MONDAY) + 1;
		int date = cal.get(Calendar.DATE);

		if (month < 10) {
			ret = "0" + (new Integer(month)).toString();
		} else {
			ret = (new Integer(month)).toString();
		}

		if (date < 10) {
			ret = ret + "0" + (new Integer(date)).toString();
		} else {
			ret = ret + (new Integer(date)).toString();
		}
		return ret;
	}
}
