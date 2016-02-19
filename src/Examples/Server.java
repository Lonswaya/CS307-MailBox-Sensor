import java.io.*;
import java.net.*;

public class Server {

	
	private Message m_message;
	
	private ServerSocket m_serverSocket;
	
	private Socket m_socket;
	
	private ObjectOutputStream m_outputStream;
	
	private ObjectInputStream m_inputStream;
	
	public Server(ServerSocket ss) {
		this.m_serverSocket = ss;
	}
	
	public Message getMessage() {
		return this.m_message;
	}
	
	public void recieveMessage() throws ClassNotFoundException, IOException {
		
		m_socket = m_serverSocket.accept();
		
		m_inputStream = new ObjectInputStream(m_socket.getInputStream());
	
		m_message = (Message)m_inputStream.readObject();
	}
	
	public void sendMessage() throws IOException {

		m_outputStream = new ObjectOutputStream(m_socket.getOutputStream());

		m_outputStream.writeObject(m_message);
		
		m_outputStream.flush();

	}
	
	
	public void setMessage(Message m) {
		this.m_message = m;
	}
	
	public void close() throws IOException
	{
		m_inputStream.close();
		m_outputStream.close();
		m_socket.close();
	}
	
	
	
	public static void main(String[] args) {
		try {
			Server server = new Server(new ServerSocket(9999));
			do {
				
				server.recieveMessage();
				
				Message tmp = server.getMessage();
				
				System.out.println("Recieved: " + tmp.getString() + " from client");
				
				server.setMessage(new Message("FUCKED UP THE MESSAGE"));
				
				server.sendMessage();
				
				server.close();
				
			} while (!server.getMessage().getString().equalsIgnoreCase("quit"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
