package test;

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
	
	static SSLSocketFactory socketFact = (SSLSocketFactory)SocketFactory.getDefault();
	static SSLServerSocketFactory serverSockFact = (SSLServerSocketFactory)ServerSocketFactory.getDefault();
	
	static synchronized ServerSocket getServerSocket(final int port) {
		try {	return Connections.serverSockFact.createServerSocket(port);	}
		catch (Exception e) {	e.printStackTrace();	}
		return null;
	}
	
	static synchronized Socket getSocket(final String ip, final int port) {		
		return Connections.getSocket(ip, port, DEFAULT_TIMEOUT);
	}
	
	static synchronized Socket getSocket(final String ip, final int port, int readTimeout) {		
		try {	
			Socket sock = socketFact.createSocket(ip, port);
			sock.setSoTimeout(readTimeout);
			return sock;
		} 
		catch (Exception e) {	e.printStackTrace();	} 
		return null;
	}

	static <T> T readObjectWithAck(ObjectInputStream in, ObjectOutputStream out, Object ack) {
		try {
			T read = Connections.<T>readObject(in);
			if (read == null) return null;
			Connections.send(out, ack);
			return read;
		} catch (Exception e) {		e.printStackTrace();	}
		return null;
	}
	
	static <T> T readObject(ObjectInputStream in) {
		try {	return (T)in.readObject();	} 
		catch (Exception e) {	e.printStackTrace();	}
		return null;
	}
		
	static void send(ObjectOutputStream out, Object toSend) {		
		try {	out.writeObject(toSend);	} 
		catch (IOException e) {		e.printStackTrace();	}
	}
	
	static <Ack> void sendWithAck(ObjectOutputStream out, ObjectInputStream in, final Object toSend) {
		Connections.<Ack>sendWithAck(out, in, toSend, DEFAULT_TIMEOUT);
	}
	
	//timeout in secs
	static <Ack> void sendWithAck(ObjectOutputStream out, ObjectInputStream in, final Object toSend, int timeOut) {
		long startTime = System.currentTimeMillis();
		while (true) { 
			Connections.send(out, toSend);
			Ack ack = (Ack)Connections.<Ack>readObject(in);
			if (ack != null || (System.currentTimeMillis() - startTime)/1000 > timeOut) break;
		}
	}
	
	static <Ack> void sendTo(final String ip, final int port, final Object toSend) {
		Socket sock = Connections.getSocket(ip, port);
		if (sock == null) {
			System.err.println("Error getting socket to: " + ip + " on port: " + port);
			return;
		}
		try {
			ObjectOutputStream out = (ObjectOutputStream)sock.getOutputStream();
			ObjectInputStream in = (ObjectInputStream)sock.getInputStream();
			Connections.<Ack>sendWithAck(out, in, toSend);
		} catch (Exception e) {		e.printStackTrace();	}
	}
}
