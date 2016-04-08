import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketInfo {
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected Socket sock;
	public SocketInfo(Socket sock, ObjectInputStream in, ObjectOutputStream out) {
		this.sock = sock;
		this.out = out;
		this.in = in;
	}
	public ObjectOutputStream getOut() {
		if (out == null) {
			try {
				return new ObjectOutputStream(sock.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else return out;
	}
}
