package com.patienttouch.sms;

import com.patienttouch.api.DlrNotificationTask;
import com.patienttouch.api.ReminderTask;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.sms.threeci.EventNotification;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SmsMsgRouter {
	private static volatile SmsMsgRouter _self;
	private static ExecutorService sendSmsService;
	private static final int minThread = 5;
	private static final int maxThread = 5;
	private static final long timeOut = 0L;
	private static final int maxQueueSize = 100;
	
	/**
	 * Private constructor to make this class singleton
	 */
	private SmsMsgRouter() {		
	}
	
	public static SmsMsgRouter getInstance() {
		if (SmsMsgRouter._self == null) {
			synchronized(SmsMsgRouter.class) {
				SmsMsgRouter._self = new SmsMsgRouter();
				_self.init();
			}
		}
		
		return SmsMsgRouter._self;
	}
	
	/**
	 * This method is used to initialize the executor
	 * 
	 */
	private void init() {
		sendSmsService = new ThreadPoolExecutor(SmsMsgRouter.minThread,SmsMsgRouter.maxThread, 
				SmsMsgRouter.timeOut, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(SmsMsgRouter.maxQueueSize));
		((ThreadPoolExecutor)sendSmsService).setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public void sendMessage(SmsMessage message) {
		ReminderTask task = new ReminderTask(message);
		sendSmsService.execute(task);
		System.out.println("Sms submited for deliverly");
	}
	
	public void processDeliveryNotification(EventNotification deliveryNotification) {
		DlrNotificationTask task = new DlrNotificationTask(deliveryNotification);
		sendSmsService.execute(task);
	}
}
