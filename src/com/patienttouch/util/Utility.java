package com.patienttouch.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.patienttouch.hibernate.ApplicationProperties;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Practice;

public class Utility {
	private static final SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat dateTime_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
	private static final Map<String, String> appProperty = new HashMap<String, String>();
	
	public static final String REMINDER_RESPONSE_TIMEOUT_IN_MS = "reminder_response_timeout_in_ms";
	public static final String WAITLIST_RESPONSE_TIMEOUT_IN_MS = "waitlist_response_timeout_in_ms";
	public static final String WAITLIST_INTERMSG_SPACING_IN_MS = "waitlist_intermsg_spacing_in_ms";
	
	public static String getDateTimeInyyyyMMdd() {
		return dateTime.format(new Date());
	}
	
	public static String getDateTimeInyyyyMMdd(Date d) {	
		return dateTime_yyyyMMdd.format(d);
	}
	
	public static String getDateInyyyyMMdd(Date d) {
		return date.format(d);
	}
	
	/**
	 * This utility method is used to return time difference in milli-seconds between two input
	 * dates.
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long diffInMs(Date d1, Date d2) {
		Calendar c1, c2;
		c1 = Calendar.getInstance();
		c1.setTime(d1);
		c2 = Calendar.getInstance();
		c2.setTime(d2);
		
		long diff = (c1.getTimeInMillis() - c2.getTimeInMillis());
		return diff;
	}
	
	public static String getProperty(String key) {
		long lastRefreshTime =0, currentTime = System.currentTimeMillis(), elapsedTime;
		String tmpStr;
		String query = "from ApplicationProperties";
		
		tmpStr = Utility.appProperty.get("refreshTime");
		if (tmpStr != null && tmpStr.matches("\\d+")) {
			lastRefreshTime = Long.parseLong(tmpStr);
		}
		
		elapsedTime = currentTime - lastRefreshTime;
		//reload properties from DB every 5 minutes
		if (tmpStr == null || (elapsedTime > 300000)) {
			synchronized(Utility.class) {
				List<ApplicationProperties> prop = Utility.getAppointmentInfo(null, query);
				if (prop != null) {
					Utility.appProperty.put("refreshTime", Long.toString(currentTime));
					for (ApplicationProperties p : prop) {
						Utility.appProperty.put(p.getKey(), p.getValue());
					}
				}
			}	
		}
		
		return Utility.appProperty.get(key);
	}
	
	/**
	 * Utility method to load property from the database
	 * @param dbsession
	 * @param query
	 * @return
	 */
	
	private static List<ApplicationProperties> getAppointmentInfo(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		List<ApplicationProperties> results = null;
		
		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.getTransaction().begin();
			}
			Query q = session.createQuery(query);

			results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
}
