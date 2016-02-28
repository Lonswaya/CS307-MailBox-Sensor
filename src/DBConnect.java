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
		ResultSet rs = connect();
		
		if (rs == null) {
			return "";
		}
		else {
			String result = rs.getString(columnNumber);
			return result;
		}
	}
	
	public static float returnLower() throws SQLException {
		int columnNumber = getColumnNumber("S_LOWER");
		ResultSet rs = connect();
		
		if (rs == null) {
			return 0;
		}
		else {
			float result = rs.getFloat(columnNumber);
			return result;
		}
	}
	
	public static float returnUpper() throws SQLException {
		int columnNumber = getColumnNumber("S_UPPER");
		ResultSet rs = connect();
		
		if (rs == null) {
			return 0;
		}
		else {
			float result = rs.getFloat(columnNumber);
			return result;
		}
	}
	
	public static boolean isConfig() throws SQLException {
		int columnNumber = getColumnNumber("CONFIG");
		ResultSet rs = connect();
		
		if (rs == null) {
			return false;
		}
		else {
			int result = rs.getInt(columnNumber);
			if (result == 0)
				return false;
			else
				return true;
		}
	}
	
	public static boolean isDetection() throws SQLException {
		int columnNumber = getColumnNumber("DETECTION");
		ResultSet rs = connect();
		
		if (rs == null) {
			return false;
		}
		else {
			int result = rs.getInt(columnNumber);
			if (result == 0)
				return false;
			else
				return true;
		}
	}
	
	public static ResultSet connect() {

		try {
			String host = "jdbc:oracle:thin:@localhost:1521:xe";
			String uName = "CS307";
			String pass = "AutoAware";

			con = DriverManager.getConnection(host, uName, pass);
			
			Statement stmt = con.createStatement();
			
			String SQL = "SELECT * FROM TABLE1";
			
			ResultSet rs = stmt.executeQuery(SQL);
			rs.next();
			
			return rs;
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}
}