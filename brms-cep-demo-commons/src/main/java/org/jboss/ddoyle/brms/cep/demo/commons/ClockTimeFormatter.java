package org.jboss.ddoyle.brms.cep.demo.commons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple helper class to format the Drools clocktime into something readable. 
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class ClockTimeFormatter {
	
	private static final String PATTERN = "yyyy-MM-dd hh:mm:SS:sss";
	
	public static String formatClockTime(long timeInMillis) {
		DateFormat df = new SimpleDateFormat(PATTERN);
		Date date = new Date(timeInMillis);
		return df.format(date);
	}
}
