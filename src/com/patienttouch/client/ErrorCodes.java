package com.patienttouch.client;

public class ErrorCodes {
	public static final int SUCCESS = 0;
	public static final int MANDATORY_PARAMETER_USERNAME_MISSING =1;
	public static final int MANDATORY_PARAMETER_PASSWORD_MISSING =2;
	public static final int INVALID_USERNAME =3;
	public static final int INVALID_PASSWORD =4;
	public static final int MANDATORY_LOGIN_INFO_MISSING =5;
	public static final int MANDATORY_PARAMETER_CONFIRM_PASSWORD_MISSING =6;
	public static final int PASSWORD_MISMATCH =7;
	public static final int MANDATORY_PARAMETER_CONTACT_INFO_MISSING =8;
	public static final int MANDATORY_PARAMETER_FIRST_NAME_MISSING =9;
	public static final int MANDATORY_PARAMETER_LAST_NAME_MISSING =10;
	public static final int MANDATORY_PARAMETER_EMAIL_MISSING =11;
	public static final int MANDATORY_PARAMETER_PHONE_MISSING =12;
	public static final int MANDATORY_PARAMETER_OFFICE_MISSING =13;
	public static final int MANDATORY_PARAMETER_OFFICE_NAME_MISSING =14;
	public static final int MANDATORY_PARAMETER_STREET_ADDRESS1_MISSING =15;
	public static final int MANDATORY_PARAMETER_CITY_MISSING =16;
	public static final int MANDATORY_PARAMETER_CITY_SHORT_MISSING =17;
	public static final int MANDATORY_PARAMETER_STATE_MISSING =18;
	public static final int MANDATORY_PARAMETER_ZIP_MISSING =19;
	public static final int MANDATORY_PARAMETER_BILLING_INFO_MISSING = 20;
	public static final int MANDATORY_PARAMETER_BILLING_STREET_ADDRESS1_MISSING =21;
	public static final int MANDATORY_PARAMETER_BILLING_CITY_MISSING =22;
	public static final int MANDATORY_PARAMETER_BILLING_STATE_MISSING =23;
	public static final int MANDATORY_PARAMETER_BILLING_ZIP_MISSING =24;
	public static final int MANDATORY_PARAMETER_PRACTICE_NAME_MISSING = 25;
	public static final int DB_CONSTRAINT_VIOLATED =26;
	public static final int SERVER_ERROR = 27;
	public static final int INVALID_PRACTICE_NAME = 28;
	public static final int MANDATORY_PARAMETER_DOCTOR_MISSING = 29;
	public static final int MANDATORY_PARAMETER_DOCTOR_FIRST_NAME_MISSING = 30;
	public static final int MANDATORY_PARAMETER_DOCTOR_LAST_NAME_MISSING = 31;
	public static final int MANDATORY_PARAMETER_DOCTOR_NICK_NAME_MISSING = 32;
	public static final int ADD_DOCTOR_INVALID_OFFICE_NAMES = 33;
	public static final int INVALID_OFFICE_NAME = 34;
	public static final int INVALID_DOCTOR_NAME = 35;
	public static final int MANDATORY_PARAMETER_TEMPLATE_MISSING = 36;
	public static final int MANDATORY_PARAMETER_TEMPLATE_NAME_MISSING = 37;
	public static final int MANDATORY_PARAMETER_TEMPLATE_TYPE_MISSING = 38;
	public static final int MANDATORY_PARAMETER_TEMPLATE_ROLE_MISSING = 39;
	public static final int MANDATORY_PARAMETER_TEMPLATE_MESSAGE_MISSING = 40;
	public static final int INVALID_SMS_TEMPLATE_TYPE = 41;
	public static final int INVALID_PRACTICE_ID = 42;
	public static final int MANDATORY_PARAMETER_PRACTICE_ID_MISSING = 43;
	public static final int INVALID_OFFICE_ID = 44;
	public static final int INVALID_DOCTOR_ID = 45;
	public static final int MANDATORY_PARAMETER_OFFICE_ID_MISSING = 46;
	public static final int MANDATORY_PARAMETER_DOCTOR_ID_MISSING = 47;
	public static final int MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING = 48;
	public static final int INVALID_SMSTEMPLATE_ID= 49;
	public static final int INVALID_PATIENT_PHONENUMBER = 50;
	public static final int MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING = 51;
	public static final int MANDATORY_PARAMETER_APPOINTEE_FIRST_NAME_MISSING = 52;
	public static final int MANDATORY_PARAMETER_APPOINTEE_LAST_NAME_MISSING = 53;
	public static final int MANDATORY_PARAMETER_APPOINTEE_DOCTOR_MISSING = 54;
	public static final int MANDATORY_PARAMETER_APPOINTEE_OFFICE_MISSING = 55;
	public static final int MANDATORY_PARAMETER_TEMPLATE_ID_MISSING = 56;
	public static final int INVALID_SMSTEMPLATE_NAME= 57;
	public static final int INVALID_APPOINTEE_ID = 58;
	public static final int UNABLE_TO_ADD_NEW_APPOINTEE = 59;
	public static final int INVALID_USER_ID = 60;
	public static final int MANDATORY_PARAMETER_USER_ID_MISSING = 61;
	public static final int MANDATORY_PARAMETER_USER_MISSING = 62;
	public static final int INVALID_USER_ROLE = 63;
	public static final int MANDATORY_PARAMETER_USER_FIRST_NAME_MISSING = 64;
	public static final int MANDATORY_PARAMETER_USER_LAST_NAME_MISSING = 65;
	public static final int SMS_TEMPLATE_ROLE_MISMATCH = 66;
	public static final int MANDATORY_PARAMETER_APPOINTMENT_DATE_MISSING = 67;
	public static final int MANDATORY_PARAMETER_APPOINTMENT_TIME_MISSING = 68;
	public static final int INVALID_WAITLIST_ID = 69;
	public static final int UNABLE_TO_ADD_TO_WAITLIST = 70;
	public static final int MANDATORY_PARAMETER_WAITLIST_ID_MISSING = 71;
	public static final int INVALID_CAMPAIGN_ID = 72;
	public static final int INVALID_APPOINTMENT_INFO_ID = 73;
	public static final int MANDATORY_PARAMETER_INTERMSG_SPACING_MISSING = 74;
	
	
	public static final String edesc[] = { 
			"Success",
			"Mandatory Parameter Username missing",
			"Mandatory Parameter Password missing",
			"Invalid Username",
			"Invalid Password",
			"Mandatory Parameter Login Info is missing",
			"Mandatory Parameter Confirm password missing",
			"Password and Confirm Password do not match",
			"Mandatory Parameter Contact Info missing",
			"Mandatory Parameter First Name missing",
			"Mandatory Parameter Last Name missing",
			"Mandatory Parameter Email missing",
			"Mandatory Parameter Phone missing",
			"Mandatory Parameter Head Office info missing",
			"Mandatory Parameter Office Name missing",
			"Mandatory Parameter Office StreetAddress1 missing",
			"Mandatory Parameter Office City missing",
			"Mandatory Parameter Office City Short missing",
			"Mandatory Parameter Office State missing",
			"Mandatory Parameter Office Zip missing",
			"Mandatory Parameter Billing Info missing",
			"Mandatory Parameter Billing StreetAddress1 missing",
			"Mandatory Parameter Billing City missing",
			"Mandatory Parameter Billing State missing",
			"Mandatory Parameter Billing Zip missing",
			"Mandatory Parameter Practice Name missing",
			"Database Constraint Violated",
			"Server Error when processing request",	
			"Invalid Practice Name",
			"Mandatory Parameter Doctor missing",
			"Mandatory Parameter Doctor First Name is missing",
			"Mandatory Parameter Doctor Last Name is missing",
			"Mandatory Parameter Doctor Nick Name is missing",
			"Invalid Offices specified for Doctor",
			"Invalid Office name",
			"First/Last/Nick name specified for Doctor invalid",
			"Mandatory Parameter Templates Missing",
			"Mandatory Parameter Template Name Missing",
			"Mandatory Parameter Template Type Missing",
			"Mandatory Parameter Template Role Missing",
			"Mandatory Parameter Template Initial Message Missing",
			"Invalid Sms Template Type",
			"Invalid Practiceid",
			"Mandatory Parameter Practiceid missing",
			"Invalid Officeid",
			"Invalid Doctorid",
			"Mandatory Parameter Officeid missing",
			"Mandatory Parameter Doctorid missing",
			"Mandatory Parameter Sms Template id missing",
			"Invalid Templateid",
			"Invalid phone number for patient",
			"Mandatory Parameter Appointee Phone number missing",
			"Mandatory Parameter Appointee First name missing",
			"Mandatory Parameter Appointee Last name missing",
			"Mandatory Parameter Appointee Doctor missing",
			"Mandatory Parameter Appointee Office missing",
			"Mandatory Parameter SmsTemplate id missing",
			"Invalid SMS Template Name",
			"Invalid Appointee id",
			"Unable to Add new Appointee to the system",
			"Invalid User id",
			"Mandatory Parameter Userid missing",
			"Mandatory Parameter User missing",
			"Invalid User Role",
			"Mandatory Parameter User First Name missing",
			"Mandatory Parameter User Last Name missing",
			"SmsTemplate role must be SUPERUSER if Practice name is not specified",
			"Mandatory Parameter Appointment Date missing",
			"Mandatory Parameter Appointment Time missing",
			"Invalid Waitlist id",
			"Unable to Add to waitlist",
			"Mandatory Parameter waitlist id missing",
			"Invalid Campaign id",
			"Invalid Appointmentinfo id"
		};

	
	
	









}
