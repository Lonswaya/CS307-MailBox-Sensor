package Example;

import java.io.IOException;

public class SendMailExample {
	public static void main(String[] args) throws IOException {
		
		
		String email = "ilepow@purdue.edu";
		String message ="This is a test";
		Sender.send(email, message);
	}
}