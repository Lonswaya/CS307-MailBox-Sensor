package cs307.purdue.edu.autoawareapp;

public class UIInfo {
	public String username, password;
	public SocketWrapper sock;
	public UIInfo(SocketWrapper sock, String username, String password) {
		this.sock = sock;
		this.username = username;
		this.password = password;
	}

}
