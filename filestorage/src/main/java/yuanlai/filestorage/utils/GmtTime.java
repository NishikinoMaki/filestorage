package yuanlai.filestorage.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GmtTime {
	public static String toString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}
}
