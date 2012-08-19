package com.patienttouch.sms.threeci;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.patienttouch.sms.SmsProvider;

public class ThreeCiSmsProvider implements SmsProvider {
	public static final int SMS_STATUS_CODE_MESSAGE_DELIVERED = 1;
	public static final int SMS_STATUS_CODE_MESSAGE_DELIVERY_FAILED = 2;
	public static final int SMS_STATUS_CODE_MESSAGE_QUEUED_AT_REMOTE_SMSC = 3;
	public static final int SMS_STATUS_CODE_MESSAGE_SUBMITTED_TO_REMOTE_SMSC = 4;
	public static final int SMS_STATUS_CODE_MESSAGE_DELIVERY_FAILED_REMOTE_SMSC_REJECT = 5;
	public static final int SMS_STATUS_CODE_MESSAGE_LOCALLY_QUEUED= 6;
	
	private static final int BUFSIZE = 512;
	private static final String USERNAME_HTTP_PARAM = "username=";
	private static final String PASSWORD_HTTP_PARAM = "password=";
	private static final String TRIGGERID_HTTP_PARAM = "trigger_id=";
	private static final String PHONENUMBER_HTTP_PARAM = "phone_number=";
	private static final String MESSAGE_HTTP_PARAM = "message=";
	private static final String CLIENTTAG_HTTP_PARAM = "client_tag=";
	private String username = "XKT03HKDtB3MKQt4P/i2Ow==";
	private String password = "UwGNR/EAFW8xLbCghIn3D7LHNCBzeq80QX5BzlxDCVI=";
	private String triggerid = "118717";

	private String apiHost = "http://platform.3cinteractive.com";
	private int apiPort = 80;
	private String apiUrl = "/api/send_message.php";
	private static volatile SmsProvider _self = null;
	
	/**
	 * Private constructor to create a single instance
	 */
	private ThreeCiSmsProvider() {	
	}
	
	/**
	 * This method is used to create a single instance of 3Ci sms provider
	 * @return
	 */
	public static SmsProvider getInstance() {
		if (ThreeCiSmsProvider._self == null) {
			synchronized(ThreeCiSmsProvider.class) {
				ThreeCiSmsProvider._self = new ThreeCiSmsProvider();
				_self.init();
			}
		}
		return _self;
	}
	
	@Override
	public String init() {
		//initialize parameters
		username = URLEncoder.encode(username);
		password = URLEncoder.encode(password);
		apiHost = "http://ec2-67-202-13-111.compute-1.amazonaws.com";
		apiPort = 8080;
		apiUrl = "patienttouchapi/api/send_message";
		return null;
	}

	@Override
	public String sendMessage(String phoneno, String message, Object userdata) {
		String url = apiHost + ":" + apiPort + "/" + apiUrl + "?";
		String encodedPhoneNumber = URLEncoder.encode(phoneno);
		String encodedMessage = URLEncoder.encode(message);
		String encodedUserData = URLEncoder.encode((String)userdata);
		
		StringBuffer content = new StringBuffer();
		content.append(USERNAME_HTTP_PARAM);
		content.append(username);
		content.append("&" + PASSWORD_HTTP_PARAM);
		content.append(password);
		content.append("&" + TRIGGERID_HTTP_PARAM);
		content.append(triggerid);
		content.append("&" + PHONENUMBER_HTTP_PARAM);
		content.append(encodedPhoneNumber);
		content.append("&" + MESSAGE_HTTP_PARAM);
		content.append(encodedMessage);
		content.append("&" + CLIENTTAG_HTTP_PARAM);
		content.append(encodedUserData);
		
		String response = ThreeCiSmsProvider.invokeApiSendMessage(url, content.toString(), "application/x-www-form-urlencoded");
		return response;
	}

	private static String invokeApiSendMessage(String url, String content, String contentType) {

        byte buffer[];
        int r;
        URL u;
        HttpURLConnection c;
        InputStream i;
        DataOutputStream o;
        StringBuffer response = null;
        try {
            // read in chunks of 512
            buffer = new byte[BUFSIZE];
            u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            //c.setRequestProperty("Content-Type", "application/json");
            c.setRequestProperty("Content-Type", contentType);
            c.setRequestProperty("Content-Length", Integer.toString(content.length()));
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);
            o = new DataOutputStream(c.getOutputStream());
            o.writeBytes(content);
            o.flush();
            o.close();

            i = c.getInputStream();
            r = i.read(buffer);
            while (r > 0) {
                if (response == null) {
                    response = new StringBuffer();
                }

                response.append(new String(buffer, 0, r));
                r = i.read(buffer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            // System.out.println(t);
        }

        return (response == null) ? null : response.toString();

    }

    /*
     * This method is used to invoke the REST query URL to fetch response from
     * lily.
     * 
     * @params url
     * 
     * @return String json response from lily for input query
     */
    /*public static String executeSearch(String url) {
        byte buffer[];
        int r;
        URL u;
        URLConnection c;
        InputStream i;
        StringBuffer response = null;
        try {
            // read in chunks of 512
            buffer = new byte[BUFSIZE];
            u = new URL(url);
            c = u.openConnection();
            i = c.getInputStream();
            r = i.read(buffer);
            while (r > 0) {
                if (response == null) {
                    response = new StringBuffer();
                }

                response.append(new String(buffer, 0, r));
                r = i.read(buffer);
            }
        } catch (Throwable t) {
            System.out.println(t);
        }

        return (response == null) ? null : response.toString();

    }*/

}
