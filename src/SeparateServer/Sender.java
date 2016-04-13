package SeparateServer;
import java.io.IOException;
import cs307.purdue.edu.autoawareapp.*;

public class Sender {
	public static void send(String email, String message) throws IOException{
		message = "\"" + message + "\"";
		ProcessBuilder pb = new ProcessBuilder("sendmail.sh", email, message);
		Process p = pb.start();	
	}
}