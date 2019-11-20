import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {
	public static void main(String[] args) {
		String username = "postgres";
		String password = "12345";
		String url = "jdbc:postgresql://localhost:5432/Guangqi";
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(url,username,password);
			ResultSet rs = null;
			Statement st = conn.createStatement();
			String sql = "select * from DEPARTMENT;";
			rs = st.executeQuery(sql);
			while(rs.next()) {
				int i = rs.getInt(1);
				String s = rs.getString(2);
				String j = rs.getString(3);
				System.out.print(i);
				System.out.print(" "+s);
				System.out.print(" "+j);
				System.out.println();
				
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
