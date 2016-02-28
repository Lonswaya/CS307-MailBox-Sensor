import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnect {
	private static Connection con;
	
	public static String returnEntry (String columnName) throws SQLException {
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
		else if (columnName == "S_TYPE") {
			columnNumber = 6;
		}
		else {
			return null;
		}
		ResultSet rs = connect();
		
		if (rs == null) {
			return "";
		}
		else {
			String result = rs.getString(columnNumber);
			return result;
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