import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;

public class DBConnect {
	private static Connection con;
	
	public static int getColumnNumber(String columnName) {
		int columnNumber = 0;
		if (columnName == "USERNAME") {
			columnNumber = 1;
		}
		else if (columnName == "IP_ADDRESS") {
			columnNumber = 2;
		}
		else if (columnName == "SENSOR_NAME") {
			columnNumber = 3;
		}
		else if (columnName == "CONFIG") {
			columnNumber = 4;
		}
		else if (columnName == "DETECTION") {
			columnNumber = 5;
		}
		else if (columnName == "S_TYPE") {
			columnNumber = 6;
		}
		else if (columnName == "S_LOWER") {
			columnNumber = 7;
		}
		else if (columnName == "S_UPPER") {
			columnNumber = 8;
		}
		else if (columnName == "DETECTION_DATE") {
			columnNumber = 9;
		}
		else if (columnName == "TIME_STAMP") {
			columnNumber = 10;
		}
		return columnNumber;
	}
	
	public static String returnEntry (String columnName) throws SQLException {
		int columnNumber = getColumnNumber(columnName);
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return "";
		}
		else {
			rs.next();
			String result = rs.getString(columnNumber);
			return result;
		}
	}
	
	public static float returnLower() throws SQLException {
		int columnNumber = getColumnNumber("S_LOWER");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return 0;
		}
		else {
			rs.next();
			float result = rs.getFloat(columnNumber);
			return result;
		}
	}
	
	public static float returnUpper() throws SQLException {
		int columnNumber = getColumnNumber("S_UPPER");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return 0;
		}
		else {
			rs.next();
			float result = rs.getFloat(columnNumber);
			return result;
		}
	}
	
	public static boolean isConfig() throws SQLException {
		int columnNumber = getColumnNumber("CONFIG");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return false;
		}
		else {
			rs.next();
			int result = rs.getInt(columnNumber);
			if (result == 0)
				return false;
			else
				return true;
		}
	}
	
	public static boolean isDetection() throws SQLException {
		int columnNumber = getColumnNumber("DETECTION");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return false;
		}
		else {
			rs.next();
			int result = rs.getInt(columnNumber);
			if (result == 0)
				return false;
			else
				return true;
		}
	}
	
	public static int addConfig(String user, String ip, String name, String type, float lower, float upper) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			Calendar c = Calendar.getInstance();
			
			String sql = "INSERT INTO TABLE1 VALUES ('" + user + "', '" + ip + "', '" + name + "', 1, 0, '" + type 
					+ "', " + lower + ", " + upper + ", '" + c.getTime() + "')";
			System.out.println(sql);
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int addDetection(String user, String ip, String name, String type, float lower, float upper) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			Calendar c = Calendar.getInstance();
			
			String sql = "INSERT INTO TABLE1 VALUES ('" + user + "', '" + ip + "', '" + name + "', 0, 1, '" + type 
					+ "', " + lower + ", " + upper + ", '" + c.getTime() + "')";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllSensorDetection(String user, String ip, String name) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE IP_ADDRESS = '" + ip + "' AND USERNAME = '" + user 
					+ "' AND SENSOR_NAME = '" + name + "' AND DETECTION = " + 1;
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllSensorConfig(String user, String ip, String name) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE IP_ADDRESS = '" + ip + "' AND USERNAME = '" + user 
					+ "' AND SENSOR_NAME = '" + name + "' AND CONFIG = " + 1;
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllUserEntries(String user) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE USERNAME = '" + user + "'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllIPEntries(String ip) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE IP_ADDRESS = '" + ip + "'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllSenosrNameEntries(String name) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE SENSOR_NAME = '" + name + "'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllConfigEntries() {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE CONFIG = 1'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllDetectionEntries() {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE DETECTION = 1'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static int removeAllTypeEntries(String type) {
		try {
			Statement stmt = connect();
			System.out.println("In");
			//String INSERT = "INSERT INTO TABLE1 ";
			
			String sql = "DELETE FROM TABLE1 WHERE S_TYPE = '" + type + "'";
			System.out.println(sql);
			
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
			return 1;
		} catch (SQLException e) {
			e.getMessage();
		}
		return -1;
	}
	
	public static Statement connect() {

		try {
			String host = "jdbc:oracle:thin:@localhost:1521:xe";
			String uName = "CS307";
			String pass = "AutoAware";

			con = DriverManager.getConnection(host, uName, pass);
			
			Statement stmt = con.createStatement();
	
			return stmt;
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}
	
	public static void main(String[] args) throws Exception {
		connect();
		//System.out.println(returnEntry("USERNAME"));
		removeAllSensorDetection("x", "127.000.000.3", "S2");
		//System.out.println(returnUpper());
	}
}