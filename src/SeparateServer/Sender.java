package SeparateServer;
import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Sender {
	public static void send(String email, String message) {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "587");
		Session session = Session.getDefaultInstance(properties,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("AutoAwareCS307@gmail.com", "MailboxSensor");
				}
			}
		);
		try {
			MimeMessage mm = new MimeMessage(session);
			mm.setFrom(new InternetAddress("AutoAwareCS307@gmail.com", "AutoAware"));
			mm.addRecipient(javax.mail.internet.MimeMessage.RecipientType.TO, new InternetAddress(email));
			mm.setSubject("AutoAware Notification");
			mm.setText(message);
			Transport.send(mm);
			System.out.println("sent email to " + email);
		} catch (Exception e) { //address or messaging exception
			e.printStackTrace();
		}
	}
	
}