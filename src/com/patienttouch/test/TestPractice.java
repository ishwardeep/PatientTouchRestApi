package com.patienttouch.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.xmappr.Xmappr;

import com.google.gson.Gson;
import com.patienttouch.api.PatientImpl;
import com.patienttouch.api.PracticeImpl;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.OfficeInfo;
import com.patienttouch.client.PracticeInfo;
import com.patienttouch.client.Request;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.Billing;
import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.CampaignStatus;
import com.patienttouch.hibernate.CampaignType;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Patient;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;
import com.patienttouch.hibernate.UserRole;
import com.patienttouch.sms.threeci.ErrorNotification;
import com.patienttouch.sms.threeci.EventNotifications;
import com.patienttouch.sms.threeci.SuccessNotification;
import com.patienttouch.util.Utility;

public class TestPractice {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Pattern pp = Pattern.compile("(\\+1)(.+)");
			
			String p = "+18582544277";
			Matcher m = pp.matcher(p);
			while (m.matches()) {
				System.out.println(m.group(0) + "-" + m.group(1) + "-" + m.group(2));
			}
			boolean b = p.matches("(\\+1)(.+)");
			System.out.println(b);
			//System.out.println(Utility.getDateTimeInyyyyMM());
			//new TestPractice().getUser("web", "admin");
			//new TestPractice().abc();
			//new TestPractice().insert();
			//new TestPractice().addSmsTemplate();
			//new TestPractice().addDoctor();
			//new TestPractice().addUser();
			//new TestPractice().addDoctorToOffice();
			//new TestPractice().select();
			//new TestPractice().getAllOffice();
			//new TestPractice().parseXml();
			new TestPractice().setupCampaign();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void select() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.addAnnotatedClass(Doctor.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();
			
			session.beginTransaction();
			
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}
				
				List<User> lu = p.getUser();
				for (User u: lu) {
					System.out.println(u.getUsername());
				}
				
				List<SmsTemplates> lt = p.getSmstemplate();
				for (SmsTemplates st : lt) {
					System.out.println(st.getName() + "[" + st.getMessage() + "]");
				}
				
				List<Doctor> d = p.getDoctor();
				for (Doctor doc: d) {
					System.out.println(doc.getFirstName() + "[" + doc.getLastName() + "]");
				}
			}
	
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("select Exception");
			e.printStackTrace();
		}
	}
	
	public void addOffice() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				/*List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}*/
				x = p;
			}
	
			List<Office> offices = x.getOffice();
			if (offices == null) {
				offices = new ArrayList<Office>();
				x.setOffice(offices);
			}
			
			Office ny = new Office();
			ny.setName("NewYork");
			ny.setMainOffice(true);
			ny.setPractice(x);
			ny.setStreetAddress1("101 street");
			ny.setCity("New York");
			ny.setCityShort("NY");
			ny.setState("NY");
			ny.setZip("12205");
			ny.setPhone("8582544280");
			
			offices.add(ny);
			
			session.update(x);
			session.getTransaction().commit();
			
			


		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}

	public void addSmsTemplate() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.addAnnotatedClass(Doctor.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				/*List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}*/
				x = p;
			}
			
			SmsTemplates sms = new SmsTemplates();
			sms.setPractice(x);
			sms.setName("default");
			sms.setMessage("<first> has an appointment with dr <doctor> on <date/time> at our <office> office. " + 
			"Reply with 1 to confirm and 2 to reschedule");
			sms.setConfirmMessage("Thank you for confirming your appointment. Please call our office for any " +
			"changes <office tn>");
			sms.setRescheduleMessage("We're sorry you are unable to make your appointment. Please call our office " +
			"to reschedule <office tn>");
			sms.setDel(0);
					
			session.save(sms);
			session.getTransaction().commit();
			
		

		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}

	public void addDoctor() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.addAnnotatedClass(Doctor.class);
			
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				/*List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}*/
				x = p;
			}
			
			Doctor d = new Doctor();
			d.setPractice(x);
			d.setFirstName("Cary");
			d.setLastName("Wilson");
			d.setNickName("Wilson");
			d.setDel(0);
					
			session.save(d);
			session.getTransaction().commit();
			
		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void addOffice2() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				/*List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}*/
				x = p;
			}
		
			Office ny = new Office();
			ny.setName("San Diego");
			ny.setMainOffice(true);
			ny.setPractice(x);
			ny.setStreetAddress1("101 street");
			ny.setCity("San Diego");
			ny.setCityShort("San");
			ny.setState("CA");
			ny.setZip("12206");
			ny.setPhone("8582544290");
			
			session.save(ny);
			session.getTransaction().commit();
			
			


		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void addUser() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(Doctor.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				/*List<Office> l = p.getOffice();
				
				for (Office o: l) {
					System.out.println(o.getName());
				}*/
				x = p;
			}
		
			User u = new User();
			u.setPractice(x);
			u.setUsername("su");
			u.setPassword("admin");
			u.setRole(UserRole.SUPERUSER);
			u.setDel(0);
			u.setFirstLogin(Date.valueOf("2012-07-18"));
			u.setLastLogin(Date.valueOf("2012-07-18"));
			
			User admin = new User();
			admin.setPractice(x);
			admin.setUsername("admin");
			admin.setPassword("admin");
			admin.setRole(UserRole.ADMIN);
			admin.setDel(0);
			admin.setFirstLogin(Date.valueOf("2012-07-18"));
			admin.setLastLogin(Date.valueOf("2012-07-18"));
			
			User web = new User();
			web.setPractice(x);
			web.setUsername("web");
			web.setPassword("admin");
			web.setRole(UserRole.WEBUSER);
			web.setDel(0);
			web.setFirstLogin(Date.valueOf("2012-07-18"));
			web.setLastLogin(Date.valueOf("2012-07-18"));
			
			session.save(u);
			//session.save(admin);
			//session.save(web);
			session.getTransaction().commit();
			
			


		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void insert() {
		Session session = null;
		SessionFactory factory;
		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.addAnnotatedClass(Doctor.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			Practice fortis = new Practice();
			fortis.setName("Escorts");
			fortis.setEmail("escort@yahoo.com");
			fortis.setPhone("8582544278");
			fortis.setDel(0);

			Office miami = new Office();
			miami.setName("Miami");
			miami.setMainOffice(true);
			miami.setPractice(fortis);
			miami.setStreetAddress1("101 street");
			miami.setCity("Miami");
			miami.setCityShort("Miami");
			miami.setState("FL");
			miami.setZip("12201");
			miami.setPhone("8582544278");

			Office sfo = new Office();
			sfo.setName("SFO");
			sfo.setMainOffice(false);
			sfo.setPractice(fortis);
			sfo.setStreetAddress1("101 street");
			sfo.setCity("San Francisco");
			sfo.setCityShort("SFO");
			sfo.setState("CA");
			sfo.setZip("12202");
			sfo.setPhone("8582544279");

			List<Office> l = new ArrayList();
			l.add(miami);
			l.add(sfo);
			fortis.setOffice(l);

			session.beginTransaction();
			session.save(fortis);
			System.out.println("Practice id [" + fortis.getPracticeid() + "]");
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void addDoctorToOffice() {
		Session session = null;
		SessionFactory factory;

		Configuration config = new Configuration();
		try {
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(Doctor.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.configure();

			factory = config.buildSessionFactory();
			session = factory.getCurrentSession();

			session.beginTransaction();
			
			Practice x = null;
			Office off = null;
			Query q = session.createQuery("from Practice where name= :name and del =0"); 
			q.setString("name","Escorts"); 
			List<Practice> results = q.list();
			 
			for (Practice p : results) { 
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				
				x = p;
			}
		
			List<Office> l = x.getOffice();
			
			for (Office o: l) {
				System.out.println(o.getName());
				if (o.getName().equalsIgnoreCase("SFO")) {
					off = o;
					break;
				}
			}
			
			Doctor d = new Doctor();
			d.setFirstName("John");
			d.setLastName("Cohen");
			d.setNickName("Cohen");
			d.setPractice(x);
			d.setDel(0);
			List<Office> offices = new ArrayList<Office>();
			offices.add(off);
			d.setOffice(offices);
			
			session.save(d);
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
	public void abc() {
		PracticeInfo request = new PracticeInfo();
		List<Map<String, Object>> reqList;
		Map<String, Object> m = new HashMap<String, Object>();
		Map<String, Object> ho = new HashMap<String, Object>();
		Map<String, Object> login = new HashMap<String, Object>();
		Map<String, Object> contact = new HashMap<String, Object>();
		
		request.setPracticeName("Fortis");
		request.setContact("John", "Rambo", "john.rambo@yahoo.com", "8588241888");
 		request.setLoginInfo("john", "1234", "1234");
		request.setHeadOffice("Miami Office", "701", "2nd Street", "Miami", "Miami", "FL", "33101", "8588241888");
		request.setBillingInfo("701", "2nd Street", "Miami", "FL", "33101");
		
		Gson gson1 = new Gson();
		System.out.println(gson1.toJson(request));
		
		
		m.put("name", "Fortis");
		
		login.put("login", "fortis_admin");
		login.put("password", "admin");
		login.put("confirm", "admin");
		m.put("login", login);
		
		contact.put("firstName", "FN");
		contact.put("lastName", "LN");
		contact.put("email", "fortis@gmail.com");
		contact.put("phone", "8585312248");
		m.put("contact", contact);
		
		ho.put("streetAddress1", "701");
		ho.put("streetAddress2", "2nd Street");
		ho.put("city", "Miami");
		ho.put("cityShort", "Miami");
		ho.put("state", "FL");
		ho.put("zip","33010");
		
		m.put("headOffice", ho);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(m));
		
		
		
		
	}
	
	
	public User getUser(String username, String password) {
		String baseurl = "http://ec2-67-202-13-111.compute-1.amazonaws.com:8080/patienttouchapi/api";
		String input = "username=web&password=admin";
		URL url;
		try {
			 baseurl = baseurl + "/login";
			 System.out.println(baseurl);
			 url = new URL(baseurl);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 conn.setRequestMethod("POST");
			 //conn.setRequestProperty("Content-Type", "application/");
			 OutputStream os = conn.getOutputStream();
			 os.write(input.getBytes());
			 os.flush();
			 if (conn.getResponseCode() != 200) {
				 //throw new RuntimeException("Failed : HTTP error code : "
					//	 + conn.getResponseCode());
				 System.out.println("Failed : HTTP error code : "
						 + conn.getResponseCode());
				 return null;
			 }
			 
			 BufferedReader br = new BufferedReader(new InputStreamReader(
			 (conn.getInputStream())));
			 
			 String output;
			 System.out.println("Output from Server .... \n");
			 while ((output = br.readLine()) != null) {
				 System.out.println(output);
			 }
			 
			 conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
	}

	public User getAllOffice() {
		boolean b = true;
		Map<String, Object> off1 = new HashMap<String, Object>();
		off1.put("a", b);
		
		System.out.println(off1);
		boolean d = (Boolean)off1.get("a");
		
		String baseurl = "http://ec2-67-202-13-111.compute-1.amazonaws.com:8080/patienttouchapi/api/office/getall?practice=Escorts";
		//String input = "username=web&password=admin";
		URL url;
		try {
			 //baseurl = baseurl + "/login";
			 System.out.println(baseurl);
			 url = new URL(baseurl);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 //conn.setRequestMethod("POST");
			 //conn.setRequestProperty("Content-Type", "application/");
			 //OutputStream os = conn.getOutputStream();
			 //os.write(input.getBytes());
			 //os.flush();
			 if (conn.getResponseCode() != 200) {
				 //throw new RuntimeException("Failed : HTTP error code : "
					//	 + conn.getResponseCode());
				 System.out.println("Failed : HTTP error code : "
						 + conn.getResponseCode());
				 return null;
			 }
			 
			 System.out.println("Output from Server .... \n");
			 
			 byte buffer[] = new byte[512]; 
			 InputStream i = conn.getInputStream();
			 StringBuffer response = null;
			 int r = i.read(buffer);
	            while (r > 0) {
	                if (response == null) {
	                    response = new StringBuffer();
	                }

	                response.append(new String(buffer, 0, r));
	                r = i.read(buffer);
	            }
			 
				 System.out.println(response.toString());
			
			 Gson gson = new Gson();
			 Response resp = gson.fromJson(response.toString(), Response.class);
			 
			 if (resp.getResults().getStatus() == ErrorCodes.SUCCESS) {
			 System.out.println(resp.getResults().getResults());
			 OfficeInfo off = new OfficeInfo();
			 off.setRequest((Map<String, Object>)resp.getResults().getResults());
			 System.out.println(off.getPracticeName());
			 }
			 conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
	}
	
	public void parseXml() {
		Xmappr xm = new Xmappr(ErrorNotification.class);
		StringReader reader;
		String response = 	"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
				"<errorNotification>" +
				"<message>Incorrect user or password.</message>" +
				"</errorNotification>";
		reader = new StringReader(response);
		ErrorNotification en = (ErrorNotification)xm.fromXML(reader);
		
		response = 
    			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
    					"<successNotification>" +
    					"<message>Queued for sending</message>" +
    					"<phoneNumber>8582544277</phoneNumber>" +
    					"<message>Test Message</message>" +
    					"<triggerId>11842</triggerId>" +
    					"</successNotification>";
		
		xm = new Xmappr(SuccessNotification.class);
		reader = new StringReader(response);
		SuccessNotification sn = (SuccessNotification) xm.fromXML(reader);
		
		response = "<?xml version='1.0' encoding='UTF-8'?> " +
"<eventNotifications>" +
	"<eventNotification type=\"DLR\">" +
		"<dateTime><![CDATA[2010-12-09 09:52:11]]></dateTime>" +
		"<messageId><![CDATA[13593809244154858787]]></messageId>" +
		"<phoneNumber><![CDATA[+1XXXXXXXXXX]]></phoneNumber>" +
		"<shortCode><![CDATA[12345]]></shortCode>" +
		"<carrierId><![CDATA[8]]></carrierId>" +
		"<carrierName><![CDATA[AT&T Mobility]]></carrierName>" +
		"<price><![CDATA[0]]></price>" +
		"<messageText><![CDATA[Hi, this is the MT sent to the end user]]></messageText>" +
		"<clientId><![CDATA[7435]]></clientId>" +
		"<clientTag><![CDATA[some_client_identifier_XXXXXXX]]></clientTag>" +
		"<statusId><![CDATA[4]]></statusId>" +
		"<statusDescription><![CDATA[Message submitted to carrier/gateway]]></statusDescription>" +
	"</eventNotification>" + 
  	"<eventNotification type=\"DLR\">" +
		"<dateTime><![CDATA[2010-12-09 09:52:11]]></dateTime>" +
		"<messageId><![CDATA[13593809244154858787]]></messageId>" +
		"<phoneNumber><![CDATA[+44XXXXXXXXXXXX]]></phoneNumber>" +
		"<shortCode><![CDATA[12345]]></shortCode>" +
		"<carrierId><![CDATA[8]]></carrierId>" +
		"<carrierName><![CDATA[AT&T Mobility]]></carrierName>" +
		"<price><![CDATA[0]]></price>" +
		"<messageText><![CDATA[Hi, this is the MT sent to the end user]]></messageText>" +
		"<clientId><![CDATA[7435]]></clientId>" +
		"<clientTag><![CDATA[some_client_identifier_XXXXXXX]]></clientTag>" +
		"<statusId><![CDATA[2]]></statusId>" +
		"<statusDescription><![CDATA[Message delivery failed]]></statusDescription>" +
		"<detailedStatusId><![CDATA[1002]]></detailedStatusId>" +
		"<detailedStatusDescription><![CDATA[Invalid destination]]></detailedStatusDescription>" +
	"</eventNotification>" + 
"</eventNotifications>";
		
		
		xm = new Xmappr(EventNotifications.class);
		reader = new StringReader(response);
		EventNotifications ens = (EventNotifications) xm.fromXML(reader);
		
		System.out.println("Parsed error response");
	}
	
	public void setupCampaign() {
		Session session = null;
		
		try {
			

			Practice fortis = PracticeImpl.getPractice(null, "Fortis", true, true, false, false);
			
			Campaign campaign = new Campaign();
			campaign.setPractice(fortis);
			campaign.setName("Reminder-1");
			campaign.setStatus(CampaignStatus.SCHEDULED);
			campaign.setType(CampaignType.REMINDER);
			campaign.setMessage("Appointment is due on 08/08");
			campaign.setScheduleTime(new Date(System.currentTimeMillis()));
			List<Doctor> doctors = fortis.getDoctor();
			Doctor doc = null;
			for(Doctor doctor : doctors) {
				doc = doctor;
				break;
			}
			
			List<Office> offices = fortis.getOffice();
			Office off = null;
			for (Office office : offices) {
				off = office;
				break;
			}
			
			List<Patient> patients = PatientImpl.getPatientDetails(null, "from Patient where phone = '8582544277'");
			Patient chandra = patients.get(0);		
			
			patients = PatientImpl.getPatientDetails(null, "from Patient where phone = '8585319107'");
			Patient sameer = patients.get(0);
			
			List<AppointmentInfo> appointments = new ArrayList<AppointmentInfo>();
			campaign.setAppointmentInfo(appointments);
			
			AppointmentInfo appInfo = new AppointmentInfo();
			appInfo.setCampaign(campaign);
			appInfo.setDoctor(doc);
			appInfo.setOffice(off);
			appInfo.setStatus(AppointmentStatus.TRYING);
			appInfo.setAppointee(chandra);
			appInfo.setAppointmentDate(new Date(System.currentTimeMillis()));
			appInfo.setAppointmentTime("10:00 am");
			
			appointments.add(appInfo);
			
			AppointmentInfo appInfo1 = new AppointmentInfo();
			appInfo1.setCampaign(campaign);		
			appInfo1.setDoctor(doc);
			appInfo1.setOffice(off);
			appInfo1.setStatus(AppointmentStatus.TRYING);
			appInfo1.setAppointee(sameer);
			appInfo1.setAppointmentDate(new Date(System.currentTimeMillis()));
			appInfo1.setAppointmentTime("10:15 am");
			
			appointments.add(appInfo1);
			
			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			session.save(campaign);
			System.out.println("Campaign id [" + campaign.getCampaignid() + "]");
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	
}
