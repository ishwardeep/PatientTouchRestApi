package com.patienttouch.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import com.patienttouch.client.ErrorCodes;

public class DbOperations {
	private volatile static SessionFactory _factory = null;
	private DbOperations() {
		
	}
	
	public static synchronized SessionFactory createSessionFactory() {
		if (_factory == null) {
			Configuration config = new Configuration();
		
			config.addAnnotatedClass(Practice.class);
			config.addAnnotatedClass(Office.class);
			config.addAnnotatedClass(User.class);
			config.addAnnotatedClass(Doctor.class);
			config.addAnnotatedClass(SmsTemplates.class);
			config.addAnnotatedClass(Billing.class);
			config.addAnnotatedClass(Patient.class);
			config.addAnnotatedClass(Campaign.class);
			config.addAnnotatedClass(AppointmentInfo.class);
			config.addAnnotatedClass(SmsMessage.class);
			config.addAnnotatedClass(Waitlist.class);
			config.addAnnotatedClass(ApplicationProperties.class);
			config.addAnnotatedClass(UserApiInvocationLog.class);
			config.configure();

			_factory = config.buildSessionFactory();
		}
		
		return _factory;
	}
	
	public static Session getDbSession() {
		Session session;
		SessionFactory factory;
		factory = DbOperations.createSessionFactory();
		
		session = factory.getCurrentSession();
		
		return session;
	}
	
	public static int addToDb(Session session, Object o) {
		int ret = ErrorCodes.SUCCESS;
		try {
			if (session == null) {
				session = DbOperations.getDbSession();
			}
			
			session.getTransaction().begin();
			
			session.save(o);
			
			session.getTransaction().commit();
		}
		catch (ConstraintViolationException e) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			ret = ErrorCodes.DB_CONSTRAINT_VIOLATED;
		}
		catch (Throwable t) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception [" + t.getMessage() + "]");
			t.printStackTrace();
			ret = ErrorCodes.SERVER_ERROR;
		}
		
		return ret;
	}
	
	public static int addToDbWithConditionalTransaction(Session session, Object o) {
		int ret = ErrorCodes.SUCCESS;
		boolean startTransaction = false;
		try {
			if (session == null) {
				session = DbOperations.getDbSession();
				startTransaction = true;
			}
			if (startTransaction) {
				session.getTransaction().begin();
			}
			session.save(o);
			
			if (startTransaction) {
				session.getTransaction().commit();
			}
		}
		catch (ConstraintViolationException e) {
			if (startTransaction && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ret = ErrorCodes.DB_CONSTRAINT_VIOLATED;
		}
		catch (Throwable t) {
			if (startTransaction && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception [" + t.getMessage() + "]");
			t.printStackTrace();
			ret = ErrorCodes.SERVER_ERROR;
		}
		
		return ret;
	}
	
	public static int updateDb(Session session, Object o) {
		int ret = ErrorCodes.SUCCESS;
		try {
			session.getTransaction().begin();
			
			session.update(o);
			
			session.getTransaction().commit();
		}
		catch (ConstraintViolationException e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ret = ErrorCodes.DB_CONSTRAINT_VIOLATED;
		}
		catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception [" + t.getMessage() + "]");
			t.printStackTrace();
			ret = ErrorCodes.SERVER_ERROR;
		}
		
		return ret;
	}
	
	public static int updateDbWithConditionalTransaction(Session session, Object o) {
		int ret = ErrorCodes.SUCCESS;
		boolean startTransaction = false;
		try {
			if (session == null) {
				session.getTransaction().begin();
				startTransaction = true;
			}
			
			session.update(o);
			
			if (startTransaction) {
				session.getTransaction().commit();
			}
		}
		catch (ConstraintViolationException e) {
			if (startTransaction && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ret = ErrorCodes.DB_CONSTRAINT_VIOLATED;
		}
		catch (Throwable t) {
			if (startTransaction && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception [" + t.getMessage() + "]");
			t.printStackTrace();
			ret = ErrorCodes.SERVER_ERROR;
		}
		
		return ret;
	}
	
	public static int deleteFromDb(Session session, Object o) {
		int ret = ErrorCodes.SUCCESS;
		try {
			session.getTransaction().begin();
			
			session.delete(o);
			
			session.getTransaction().commit();
		}
		catch (ConstraintViolationException e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			ret = ErrorCodes.DB_CONSTRAINT_VIOLATED;
		}
		catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception [" + t.getMessage() + "]");
			t.printStackTrace();
			ret = ErrorCodes.SERVER_ERROR;
		}
		
		return ret;
	}
}
