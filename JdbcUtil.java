import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtil {
    private static String dirverName;
    private static String url;
    private static String username;
    private static String password;
    static{
        try {
            InputStream inputStream = JdbcUtil.class.getClassLoader()
                    .getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            dirverName = properties.getProperty("driverName");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            System.out.println(dirverName);
            System.out.println(url);
            System.out.println(username);
            System.out.println(password);
            Class.forName(dirverName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        Connection conn=null;
        try {
            conn=DriverManager.getConnection(url, username, password);
            System.out.println("connected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
}
