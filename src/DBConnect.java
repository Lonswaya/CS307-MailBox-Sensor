import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

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
			String sql = "INSERT INTO TABLE1 " + "VALUES ('d', '127.000.000.1', 'L', 1, 0, 'LIGHT', 0, 100)";
			int a = stmt.executeUpdate(sql);
			System.out.println(a);
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
		addConfig("a", "127.000.000.2", "S1", "LIGHT", 0, 255);
	}
}