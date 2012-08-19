package com.patienttouch.sms;

import java.util.HashMap;
import java.util.Map;

import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Patient;

public class SmsFormater {
	public static final int TEMPLATE_VARIABLE_IDX = 0;
	public static final int TEMPLATE_VARIABLE_PRIORITY = 1;
	public static final int TEMPLATE_VARIABLE_MAPPING_KEY = 2;
	
	private static final int MAX_SMS_LENGTH = 160;
	private static String START_CHAR = "\\[";
	private static String END_CHAR = "\\]";
	
	private static final String variableMapping_old[][] = { 
			{SmsFormater.START_CHAR + "First" + SmsFormater.END_CHAR,"1","firstName" },
			{SmsFormater.START_CHAR + "Office" + SmsFormater.END_CHAR,"2","officeName"},
			{SmsFormater.START_CHAR + "Doctor" + SmsFormater.END_CHAR, "3","doctor"},
			{SmsFormater.START_CHAR + "Time" + SmsFormater.END_CHAR,"4","time"},
			{SmsFormater.START_CHAR + "Date" + SmsFormater.END_CHAR, "5","date"},
			{SmsFormater.START_CHAR + "Day" + SmsFormater.END_CHAR,"6","day"},
			{SmsFormater.START_CHAR + "City Abbr" + SmsFormater.END_CHAR,"7","officeCityShort"},
			{SmsFormater.START_CHAR + "City" + SmsFormater.END_CHAR,"8","officeCity"},
			{SmsFormater.START_CHAR + "Addr" + SmsFormater.END_CHAR,"9","officeAddress"},
			{SmsFormater.START_CHAR + "Last" + SmsFormater.END_CHAR,"10","lastName"},
			{SmsFormater.START_CHAR + "Practice" + SmsFormater.END_CHAR,"11","practice" },
			{SmsFormater.START_CHAR + "State" + SmsFormater.END_CHAR,"12","officeState"},
			{SmsFormater.START_CHAR + "Office TN" + SmsFormater.END_CHAR,"13","officeTN"}
			
		};
	
	private static final String variableMapping[][] = { 
		{"First","1","firstName" },
		{"Office","2","officeName"},
		{"Doctor", "3","doctor"},
		{"Time","4","time"},
		{"Date", "5","date"},
		{"Day","6","day"},
		{"City Abbr","7","officeCityShort"},
		{"City","8","officeCity"},
		{"Addr","9","officeAddress"},
		{"Last","10","lastName"},
		{"Practice","11","practice" },
		{"State","12","officeState"},
		{"Office TN","13","officeTN"}
		
	};
	
	public static String buildMessage(String sms, Map<String, String> values) {
		String tempMsg = sms;
		System.out.println("Sms [" + sms + "]");
		for (int i=0; i < SmsFormater.variableMapping.length; i++ ) {
			System.out.println("Search [" + SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_IDX] + "] key [" +
					SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_MAPPING_KEY]);
			//if (tempMsg.matches("(.)(" + SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_IDX] + ")(.)")) {
			if (tempMsg.indexOf(SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_IDX]) != -1) {
				tempMsg = tempMsg.replaceFirst(SmsFormater.START_CHAR + SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_IDX] + 
						SmsFormater.END_CHAR, 
						values.get(SmsFormater.variableMapping[i][SmsFormater.TEMPLATE_VARIABLE_MAPPING_KEY]));
			}
			if (tempMsg.indexOf("[") == -1) {
				break;
			}
			System.out.println("Msg [" + tempMsg + "]");
		}
		return tempMsg;
	}
	
	/**
	 * This api is used to create SMS message by replacing variables in the message template with
	 * actual values
	 * 
	 * @param messageTemplate
	 * @param practiceName
	 * @param patient
	 * @param office
	 * @param doctor
	 * @return
	 */
	public static String formatSms(String messageTemplate, String practiceName, String appointmentDate,
			String appointmentTime, Patient patient, Office office, Doctor doctor) {
		Map<String, String> variableValues = new HashMap<String, String>();
		
		variableValues.put("practice", practiceName);
		
		variableValues.put("firstName", patient.getFirstName());
		variableValues.put("lastName", patient.getLastName());
		
		variableValues.put("officeName", office.getName());
		variableValues.put("officeAddress", office.getStreetAddress1());
		variableValues.put("officeCity", office.getCity());
		variableValues.put("officeCityShort", office.getCityShort());
		variableValues.put("officeState", office.getState());
		variableValues.put("officeTN", office.getPhone());
		
		variableValues.put("doctor", doctor.getNickName());
		
		variableValues.put("date",appointmentDate);
		variableValues.put("time",appointmentTime);
		variableValues.put("day","");
		
		String sms = SmsFormater.buildMessage(messageTemplate, variableValues);
		
		return sms;
	}
}
