import java.io.IOException;

public class Sender {
	public static void send(String email, String message) throws IOException{
		message = "\"" + message + "\"";
		ProcessBuilder pb = new ProcessBuilder("sendmail.sh", email, message);
		Process p = pb.start();	
	}
}