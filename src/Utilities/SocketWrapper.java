package Utilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper {
	public ObjectInputStream in;
	public ObjectOutputStream out;
	public Socket sock;
	public SocketWrapper(Socket newSocket) {
		try {
			sock = newSocket;
			//we get out instantly, so no problem
			out = new ObjectOutputStream(newSocket.getOutputStream());
		} catch (Exception e) {
			in = null;
			out = null;
		}
		//we have to create a new thread, as the inputstream will be blocked until the server flushes with its outputstream
		//so we will have to check to see if it is null. If it is null, we must continue waiting (i.e. continue in the loop)
		new Thread(new Runnable() {
			public void run() {
				try {
					if (sock != null) {
						in = new ObjectInputStream(sock.getInputStream());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public SocketWrapper(Socket sock, ObjectInputStream in, ObjectOutputStream out) {
		this.sock = sock;
		this.out = out;
		this.in = in;
	}
	public boolean IsAlive() {
		return sock.isConnected() && sock.isClosed();
	}
}
