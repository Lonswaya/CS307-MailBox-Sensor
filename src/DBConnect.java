import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

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
	
	public static ArrayList<String> getAllEntries() throws SQLException {
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return null;
		}

		ArrayList<String> result = new ArrayList<String>();
		
		while (rs.next()){
			String row = rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getInt(4) + " " + rs.getInt(5) 
					+ " " + rs.getString(6) + " " + rs.getFloat(7) + " " + rs.getFloat(8) + " " + rs.getString(9);
			result.add(row);
		}

		return result;
	}
	
	public static ArrayList<String> getAllIPAddress () throws SQLException {
		int columnNumber = getColumnNumber("IP_ADDRESS");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return null;
		}

		ArrayList<String> result = new ArrayList<String>();
		
		while (rs.next()){
			result.add(rs.getString(columnNumber));
		}

		return result;
	}
	
	public static ArrayList<String> getAllSensorNames () throws SQLException {
		int columnNumber = getColumnNumber("SENSOR_NAME");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return null;
		}

		ArrayList<String> result = new ArrayList<String>();
		
		while (rs.next()){
			result.add(rs.getString(columnNumber));
		}

		return result;
	}
	
	public static ArrayList<Float> getAllLowerValues() throws SQLException {
		int columnNumber = getColumnNumber("S_LOWER");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return null;
		}

		ArrayList<Float> result = new ArrayList<Float>();
		
		while (rs.next()){
			result.add(rs.getFloat(columnNumber));
		}

		return result;
	}

	
	public static ArrayList<Float> getAllUpperValues() throws SQLException {
		int columnNumber = getColumnNumber("S_UPPER");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return null;
		}

		ArrayList<Float> result = new ArrayList<Float>();
		
		while (rs.next()){
			result.add(rs.getFloat(columnNumber));
		}

		return result;
	}
	
	public static boolean isConfig(String user, String ip, String name) throws SQLException {
		int columnNumber = getColumnNumber("CONFIG");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1 WHERE USERNAME = '" + name + "' AND IP_ADDRESS = '" + ip + "' AND SENSOR_NAME = '" 
				+ name + "'";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return false;
		}
		else {
			rs.next();
			int result = rs.getInt(columnNumber);
			if (rs.next()) {
				System.out.println("Multiple entries available");
				return false;
			}
			if (result == 0)
				return false;
			return true;
		}
	}
	
	public static boolean isDetection(String user, String ip, String name) throws SQLException {
		int columnNumber = getColumnNumber("DETECTION");
		Statement stmt = connect();
		
		String SQL = "SELECT * FROM TABLE1 WHERE USERNAME = '" + name + "' AND IP_ADDRESS = '" + ip + "' AND SENSOR_NAME = '" 
				+ name + "'";
		
		ResultSet rs = stmt.executeQuery(SQL);
		
		if (rs == null) {
			return false;
		}
		else {
			rs.next();
			int result = rs.getInt(columnNumber);
			if (rs.next()) {
				System.out.println("Multiple entries available");
				return false;
			}
			if (result == 0)
				return false;
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
		//removeAllSensorDetection("x", "127.000.000.3", "S2");
		//returnLower();
		System.out.println(getAllEntries());
	}
}