import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {
    private static Connection getConnection(){
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")){
            p.load(in);

            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        }
        catch (Exception e){
            System.out.println("Exception: " + e.getClass().getName() + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Connection con = getConnection();
        if (con == null){
            return;
        }


    }
}