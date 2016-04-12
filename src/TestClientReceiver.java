package src;
import cs307.purdue.edu.autoawareapp.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TestClientReceiver implements Runnable {

	Socket sock = null;
	
	public TestClientReceiver(Socket s) {
		this.sock = s;
	}
	
	
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Message msg = null;
		try {
			msg = (Message)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		switch(msg.type) {
			
			case VIDEO:
				System.out.println("Got video msg");
				break;
			case AUDIO:
				System.out.println("Got audio msg");
				break;
			case LIGHT:
				System.out.println("Got light msg");
				break;
			case ADD_SENSOR:
				System.out.println("Got add_sensor");	
				break;
			case CONFIG:
				System.out.println("Got config msg");
				break;
			case STREAMING:
				System.out.println("Got streaming");
				break;
			default:
				System.out.println("Fukd up");
				break;		
		}
		System.out.println(msg);		
		
	}

		
	
	
	
}
