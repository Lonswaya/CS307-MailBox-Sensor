package SeparateServer;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class Sender {
	public static void send(String email, String message) {
		Properties properties = System.getProperties();
		properties.setProperty("mailsmtp.host", "localhost");
		Session session = Session.getDefaultInstance(properties);
		
		try {
			MimeMessage mm = new MimeMessage(session);
			mm.setFrom(new InternetAddress("AutoAware@gmail.com"));
			mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			mm.setSubject("AutoAware Notification");
			mm.setText(message);
			Transport.send(mm);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
}