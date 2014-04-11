package ws.hoyland.util;

import java.util.Random;

public class IdentificationCardCodeUtil {
	// 18位身份证中，各个数字的生成校验码时的权值
	private final static int[] VERIFY_CODE_WEIGHT_ARRAY = { 7, 9, 10, 5, 8, 4,
			2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	// 18位身份证中最后一位校验码
	private final static char[] VERIFY_CODE_ARRAY = { '1', '0', 'X', '9', '8',
			'7', '6', '5', '4', '3', '2' };
	private final static Random random = new Random();

	public static void main(String[] args) {
		String code = getRandomAreaCode() + getRandomBirthdayCode(1980, 2000)
				+ getRandomSequenceCode();
		String randomCardCode = code + calculateVerifyCode(code);
		System.out.println(randomCardCode);
		System.out.println(verifyIdentificationCardCode(randomCardCode));
	}

	public static boolean verifyIdentificationCardCode(CharSequence cardNumber) {
		if (cardNumber.length() != 18)
			return false;
		for (int i = 0; i < 17; i++) {
			char ch = cardNumber.charAt(i);
			if (ch < '0' && ch > '9')
				return false;
		}
		return verifyAreaCode(cardNumber.subSequence(0, 6))
				&& verifyBirthdayCode(cardNumber.subSequence(6, 14))
				&& verifyVerifyCode(cardNumber);
	}

	public static boolean verifyAreaCode(CharSequence areaCode) {
		// 需对照居民身份证行政区划代码表，不同版本会有不同
		return true;
	}

	public static boolean verifyBirthdayCode(CharSequence birthdayCode) {
		if (birthdayCode.length() != 8)
			return false;
		try {
			int year = Integer.valueOf(birthdayCode.subSequence(0, 4)
					.toString());
			int month = Integer.valueOf(birthdayCode.subSequence(4, 6)
					.toString());
			int day = Integer
					.valueOf(birthdayCode.subSequence(6, 8).toString());
			if (month < 1
					|| month > 12
					|| day < 1
					|| day > 31
					|| ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30)
					|| (month == 2 && (((year) % 4 > 0 && day > 28) || day > 29)))
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean verifyVerifyCode(CharSequence cardCode) {
		int VerifyCode = 0;
		try {
			for (int i = 0; i < 17; i++)
				VerifyCode += Integer.parseInt(String.valueOf(cardCode
						.charAt(i))) * VERIFY_CODE_WEIGHT_ARRAY[i];
		} catch (NumberFormatException e) {
			return false;
		}
		return VERIFY_CODE_ARRAY[VerifyCode % 11] == cardCode.charAt(17);
	}

	public static String getRandomAreaCode() {
		// 需对照居民身份证行政区划代码表，不同版本会有不同
		String provinceCode = "10";
		String cityCode = "01";
		String regionCode = "01";
		return provinceCode + cityCode + regionCode;
	}

	public static String getRandomBirthdayCode(int startYear, int endYear) {
		if (startYear < 1900 || endYear < startYear)
			return null;
		int year = startYear + random.nextInt(endYear - startYear + 1);
		int month = random.nextInt(12) + 1;
		int day = 0;
		if (month == 2)
			if (isLeapYear(year))
				day = random.nextInt(29) + 1;
			else
				day = random.nextInt(28) + 1;
		else if (month == 1 || month == 3 || month == 5 || month == 7
				|| month == 8 || month == 10 || month == 12)
			day = random.nextInt(31) + 1;
		else
			day = random.nextInt(30) + 1;
		return String.valueOf(year * 10000 + month * 100 + day);
	}

	public static boolean isLeapYear(int year) {
		return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
	}

	public static String getRandomSequenceCode() {
		return String.valueOf(1000 + random.nextInt(1000)).substring(1);
	}

	public static char calculateVerifyCode(CharSequence cardCode) {
		int VerifyCode = 0;
		for (int i = 0; i < 17; i++)
			VerifyCode += Integer.parseInt(String.valueOf(cardCode.charAt(i)))
					* VERIFY_CODE_WEIGHT_ARRAY[i];
		return VERIFY_CODE_ARRAY[VerifyCode % 11];
	}
}
