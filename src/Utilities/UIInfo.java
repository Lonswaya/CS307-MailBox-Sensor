package Utilities;

public class UIInfo {
	public String username, password;
	public SocketWrapper sock;
	public UIInfo(SocketWrapper sock, String username, String password) {
		this.sock = sock;
		this.username = username;
		this.password = password;
	}

}
