package Example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class Connections {
	
	final static int DEFAULT_TIMEOUT = 5;
	
	//TODO: Use ssl eventually
	
	//static SSLSocketFactory socketFact = (SSLSocketFactory)SocketFactory.getDefault();
	//static SSLServerSocketFactory serverSockFact = (SSLServerSocketFactory)ServerSocketFactory.getDefault();
	
	static SocketFactory socketFact 		  = SocketFactory.getDefault();
	static ServerSocketFactory serverSockFact = ServerSocketFactory.getDefault();
	
	//returns a ServerSocket on that port. No need to check the return value.
	static synchronized ServerSocket getServerSocket(final int port) {
		ServerSocket ss = null;
		while (ss == null) {
			try {	ss = Connections.serverSockFact.createServerSocket(port, 1000);	}
			catch (Exception e) {	e.printStackTrace();    }
		}
		return ss;
	}
	
	//returns a Socket connecting to a ServerSocket. No need to check the return value.
	static synchronized Socket getSocket(final String ip, final int port) {		
		Socket sock = null;
		while (sock == null) {
			try {	sock = Connections.socketFact.createSocket(ip, port);	}
			catch (Exception e) {	e.printStackTrace();    }
		}
		return sock;
	}
	
	//Reads an object of type T. No need to check return value.
	//Usage: Message msg = Connections.<Message>readObject(in);
	static <T> T readObject(ObjectInputStream in) {
		T read = null;
		while (read == null) {
			try {	read = (T)in.readObject();	}
			catch (Exception e) {	e.printStackTrace();    }
		}
		return read;
	}
	
	//same as before but only takes a Socket.
	//no need to check return values
	static <T> T readObject(Socket sock) {
		ObjectInputStream in = Connections.getInputStream(sock);
		return Connections.readObject(in);
	}
		
	//ensures that the message is sent 
	static void send(ObjectOutputStream out, Object toSend) {
		boolean sent = false;
		while (!sent) {
			try {
				out.writeObject(toSend);
				sent = true;
			} catch (Exception e) {	e.printStackTrace();	}
		}
	}
	
	//should be private but needs to be static. Try not to use
	static ObjectOutputStream getOutputStream(Socket sock) {
		ObjectOutputStream out = null;
		while (out == null) {
			try {	out = new ObjectOutputStream(sock.getOutputStream());	}
			catch (Exception e) {	e.printStackTrace();    }
		}
		return out;
	}
	
	//should be private but needs to be static. Try not to use
	static ObjectInputStream getInputStream(Socket sock) {
		ObjectInputStream in = null;
		while (in == null) {
			try {	in = new ObjectInputStream(sock.getInputStream());	}
			catch (Exception e) {	e.printStackTrace();    }
		}
		return in;
	}
	
	//ensures that a message is sent
	static void send(Socket sock, Object toSend) {
		ObjectOutputStream out = Connections.getOutputStream(sock);
		Connections.send(out, toSend);
	}
	
	//closes a socket and catches exceptions for you so closing is only one line
	static void closeSocket(Socket sock) {
		try {	sock.close();	} 
		catch (Exception e) {    e.printStackTrace();    }
	}
	
}
