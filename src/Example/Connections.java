package Example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.Date;

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
	public static synchronized ServerSocket getServerSocket(final int port) {
		ServerSocket ss = null;
		while (ss == null) {
			try {	ss = Connections.serverSockFact.createServerSocket(port, 1000);	}
			catch (Exception e) {	
				System.err.println("Issue getting server socket, " + e.toString());    
				break;
			}
		}
		return ss;
	}
	
	//returns a Socket connecting to a ServerSocket. No need to check the return value.
	public static synchronized Socket getSocket(final String ip, final int port) {		
		Socket sock = null;
		while (sock == null) {
			try {	sock = Connections.socketFact.createSocket(ip, port);	}
			catch (Exception e) {		
				System.err.println("Issue getting socket, " + e.toString());   
			}
		}
		return sock;
	}
	
	//ensures that the message is sent, if it is not sent within a reasonable timeout, it stops and returns false
	public static synchronized Socket getSocket(final String ip, final int port, long timeout) {
			
		boolean stop = false;
		long time = new Date().getTime();
		Socket sock = null;
		while (!stop) {
			try {
				if (new Date().getTime() - time > timeout) {
					return null;
				}
				sock = Connections.socketFact.createSocket(ip, port);
				return sock;
			} catch (Exception e) {	
				System.err.println("Issue getting socket, retrying  " + e.toString()); 	
				return null;
			}
		}
		return null;
	}
	
	//Reads an object of type T. No need to check return value.
	//Usage: Message msg = Connections.<Message>readObject(in);
	public static synchronized <T> T readObject(ObjectInputStream in) {
		T read = null;
		while (read == null) {
			try {	read = (T)in.readObject();	}
			catch (Exception e) {	
				System.err.println("Issue reading object, retrying "  + e.toString());     
				break;
			}
		
		}
		return read;
	}
	//Reads an object of type T. No need to check return value.
	//Usage: Message msg = Connections.<Message>readObject(in);
	public static synchronized <T> T readObject(ObjectInputStream in, long timeout) {
		T read = null;
		long time = new Date().getTime();
		while (time - new Date().getTime() < timeout) {
			try {	read = (T)in.readObject();	}
			catch (Exception e) {	
				System.err.println("Issue reading object, retrying until timeout "  + e.toString());     
			
			}
		}
		return read;
	}
	

	
	//same as before but only takes a Socket.
	//no need to check return values
	public static synchronized <T> T readObject(Socket sock) {
		ObjectInputStream in = Connections.getInputStream(sock);
		return Connections.readObject(in);
	}
	
	
	//ensures that the message is sent 
	public static synchronized boolean send(ObjectOutputStream out, Object toSend) {
		try {
			out.writeObject(toSend);
			out.flush();
			out.reset();
			return true;
		} catch (Exception e) {	
			System.err.println("Issue sending object, retrying " + e.toString()); 	
			return false;
		}
	}
	
	
	
	//should be private but needs to be static. Try not to use
	private static synchronized ObjectOutputStream getOutputStream(Socket sock) {
		ObjectOutputStream out = null;
		while (out == null) {
			try {	out = new ObjectOutputStream(sock.getOutputStream());	}
			catch (Exception e) { 
				System.err.println("Issue getting outputstream, retrying " + e.toString());     
				break;
			}
		}
		return out;
	}
	
	//should be private but needs to be static. Try not to use
	public static synchronized ObjectOutputStream getOutputStream(Socket sock, long timeout) {
		ObjectOutputStream out = null;
		long time = new Date().getTime();
		while (out == null && new Date().getTime() - time < timeout) {
			try {	out = new ObjectOutputStream(sock.getOutputStream());	}
			catch (Exception e) { 
				System.err.println("Issue getting outputstream, retrying until timeout " + e.toString());
				break;
			}
		}
		return out;
	}
	
	
	//should be private but needs to be static. Try not to use
	public static synchronized ObjectInputStream getInputStream(Socket sock) {
		ObjectInputStream in = null;
		while (in == null) {
			try {	in = new ObjectInputStream(sock.getInputStream());	}
			catch (Exception e) {	
				System.err.println("Issue getting inputstream, retrying " + e.toString());    
				break;
			}
		}
		return in;
	}
	
	/*//should be private but needs to be static. Try not to use
	private static ObjectInputStream getInputStream(Socket sock, long timeout) {
		ObjectInputStream in = null;
		while (in == null) {
			try {	in = new ObjectInputStream(sock.getInputStream());	}
			catch (Exception e) {	System.err.println("Issue getting inputstream, retrying");    }
		}
		return in;
	}*/
	
	//ensures that a message is sent
	public static synchronized void send(Socket sock, Object toSend) {
		ObjectOutputStream out = Connections.getOutputStream(sock);
		Connections.send(out, toSend);
	}
	
	//confirms that you can even connect to a server, even if you already have a socket
	public static synchronized boolean sendAndCheck(ObjectOutputStream out, Object toSend, long timeout) {
			long time = new Date().getTime();
			while (true) {
				try {
					if (new Date().getTime() - time > timeout) {
						return false; //TIMEOUT
					}
					
					out.writeObject(toSend);
					out.flush();
					out.reset();
					return true;
				} catch (Exception e) {
					System.err.println("Issue sendinng from socket, retrying until timeout " + e.toString()); 
					break;
				}
			}
			return false;
				
		}
	
	//confirms that you can even connect to a server, even if you already have a socket
	public static synchronized boolean sendAndCheck(Socket sock, Object toSend, long timeout) {
		long time = new Date().getTime();
		while (true) {
			try {
				if (new Date().getTime() - time > timeout) {
					return false; //TIMEOUT
				}
				ObjectOutputStream out = Connections.getOutputStream(sock, 1000);
				out.writeObject(toSend);
				out.flush();
				out.reset();
				return true;
			} catch (Exception e) {
				System.err.println("Issue sendinng from socket, retrying until timeout " + e.toString()); 
				break;
			}
		}
		return false;
			
	}
	
	
	//confirms that you can even connect to a server
	public static synchronized boolean sendAndCheck(String ip, int port, Object toSend, long timeout) {
		long time = new Date().getTime();
		while (true) {
			if (new Date().getTime() - time > timeout) {
				return false; //TIMEOUT
			}
			try {
				Socket sock = Connections.getSocket(ip, port, 1000);
				boolean b = sendAndCheck(sock, toSend, 1000);
				sock.close();
				return b;
			} catch (Exception e) {
				System.err.println("Issue getting socket to send, retrying until timeout " + e.toString()); 
				break;
			}
		}
		return false;
			
	}
	
	//closes a socket and catches exceptions for you so closing is only one line
	public static synchronized void closeSocket(Socket sock) {
		try {	sock.close();	} 
		catch (Exception e) { 
			System.err.println("Issue closing socket, retrying " + e.toString());   
			//break;
		}
	}
	
}
