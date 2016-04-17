package SeparateServer;
import java.io.IOException;
import cs307.purdue.edu.autoawareapp.*;

public class Sender {
	public static void send(String email, String message) {
		message = "\"" + message + "\"";
		ProcessBuilder pb = new ProcessBuilder("sendmail.sh", email, message);
		try {
			Process p = pb.start();	
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}