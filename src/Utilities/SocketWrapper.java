package Utilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper {
	public ObjectInputStream in;
	public ObjectOutputStream out;
	public Socket sock;
	public SocketWrapper(Socket sock) {
		try {
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			in = null;
			out = null;
		}
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
