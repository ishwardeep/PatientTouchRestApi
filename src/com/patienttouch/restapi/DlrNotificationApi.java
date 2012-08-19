package com.patienttouch.restapi;

import java.io.StringReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.xmappr.Xmappr;

import com.patienttouch.api.DlrNotificationTask;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.sms.threeci.EventNotification;
import com.patienttouch.sms.threeci.EventNotifications;
import com.patienttouch.sms.threeci.SuccessNotification;


@Path("/dlrnotification")
public class DlrNotificationApi {
	
	@POST
	@Consumes("application/xml")
	@Produces({MediaType.APPLICATION_XML})
	public String processDlrNotification(String content) {
		Xmappr xm;
		String response = null;
		StringReader reader;
		EventNotifications en;
        
		try {
        	System.out.println("DLR Notification from 3ci server. content [" + content + "]");
        	
        	reader = new StringReader(content);
        	xm = new Xmappr(EventNotifications.class);
			en = (EventNotifications) xm.fromXML(reader);
			
			for (EventNotification ev : en.eventNotification) {
				if (ev.clientTag == null || ev.clientTag.isEmpty()) {
					System.out.println("No value for clientTag parameter. Unable to parse DlrNotification");
					return null;
				}
				
				SmsMsgRouter.getInstance().processDeliveryNotification(ev);
				//DlrNotificationTask dlrTask = new DlrNotificationTask(ev);
				//dlrTask.run();			
			}
			
        	response = "OK";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}
}