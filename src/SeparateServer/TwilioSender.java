package SeparateServer;
//You may want to be more specific in your imports 
import java.util.*; 
import org.apache.http.*;
import org.apache.http.message.*;
import com.twilio.sdk.*; 
import com.twilio.sdk.resource.factory.*; 
import com.twilio.sdk.resource.instance.*; 
import com.twilio.sdk.resource.list.*; 
import cs307.purdue.edu.autoawareapp.*;
public class TwilioSender { 
	// Find your Account Sid and Token at twilio.com/user/account 
	public static final String ACCOUNT_SID = "ACc00d942b6e40dd03ef247140a4638d15"; 
	public static final String AUTH_TOKEN = "1e705b0e12a0e93aa06f692e659feffa"; 
	
	public static void send(String phoneNumber, String textMessage) throws TwilioRestException { 
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN); 
	
		// Build the parameters 
		List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("To", phoneNumber)); 
		params.add(new BasicNameValuePair("From", "+17655889993")); 
		params.add(new BasicNameValuePair("Body", textMessage));   
		MessageFactory messageFactory = client.getAccount().getMessageFactory(); 
		com.twilio.sdk.resource.instance.Message message = messageFactory.create(params); 
		System.out.println(message.getSid()); 
	} 
}