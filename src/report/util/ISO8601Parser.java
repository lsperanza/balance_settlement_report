package report.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class ISO8601Parser {

	public static String toISO8601String(Date date) {
		if (date == null) {
			return null;
		}
		DateTime dt = new DateTime(date);
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

		return fmt.print(dt);
	}

	public static Date parse(String input) throws java.text.ParseException {
		if (input == null) {
			return null;
		}
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTime dt = fmt.parseDateTime(input);
		return dt.toDate();
	}

	public static String toUTCString(Date date) {
		if (date == null) {
			return null;
		}
		DateTime dt = new DateTime(date);
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return fmt.print(dt);
	}

	public static Date parseUTCString(String input)
			throws java.text.ParseException {
		if (input == null) {
			return null;
		}
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		if (input.length() == 20) {
			format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		}
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		DateTime dt = fmt.parseDateTime(input);
		return dt.toDate();
	}

}
