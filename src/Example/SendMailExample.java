import java.io.IOException;

public class SendMailExample {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Usage: java SendMailExample email \"message\"");
			return;
		}
		
		String email = args[0];
		String message = "\"" + args[1] + "\""; //add quotes back, message becomes "message"
		ProcessBuilder pb = new ProcessBuilder("sendmail.sh", email, message);
		Process p = pb.start();	
	}
}