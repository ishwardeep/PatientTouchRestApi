package com.patienttouch.restapi;

import java.io.StringReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.xmappr.Xmappr;

import com.patienttouch.api.PostNotificationTask;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.sms.threeci.CancelSubscriptionNotification;
import com.patienttouch.sms.threeci.EventNotification;
import com.patienttouch.sms.threeci.EventNotifications;
import com.patienttouch.sms.threeci.SubscriptionNotification;


@Path("/postnotification")
public class PostNotificationApi {
	
	@POST
	@Consumes("application/xml")
	@Produces({MediaType.APPLICATION_XML})
	public String processPostNotification(String content) {
		Xmappr xm;
		String response = null;
		StringReader reader;
		EventNotifications en;
		
        try {
        	System.out.println("Post Notification from 3ci server. content [" + content + "]");
        	
        	reader = new StringReader(content);
        	xm = new Xmappr(EventNotifications.class);
			en = (EventNotifications) xm.fromXML(reader);
			
			for (SubscriptionNotification sn : en.subscriptionNotification) {		
				SmsMsgRouter.getInstance().processPostNotification(sn, null);
				//PostNotificationTask postTask = new PostNotificationTask(sn, null);
				//postTask.run();			
			}
			
			for (CancelSubscriptionNotification csn : en.cancelSubscriptionNotification) {
				SmsMsgRouter.getInstance().processPostNotification(null, csn);
				//PostNotificationTask postTask = new PostNotificationTask(null, csn);
				//postTask.run();
			}
        	response = "OK";
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
}