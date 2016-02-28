import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnect {

	public static void main (String[] args) {

		Connection con;
		try {
			String host = "jdbc:oracle:thin:@localhost:1521:xe";
			String uName = "CS307";
			String pass = "AutoAware";

			con = DriverManager.getConnection(host, uName, pass);
			
			Statement stmt = con.createStatement();
			
			String SQL = "SELECT * FROM TABLE1";
			
			ResultSet rs = stmt.executeQuery(SQL);
			rs.next();
			
			System.out.println(rs.getString(1));
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}
}