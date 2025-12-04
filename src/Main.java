import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

public class Main {
    private static Connection getConnection(){
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("app.properties")){
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




    // STEP 6 ===========================================================================================================================================================

    public static void createRestaurantMenuView() {
        String sql =
                "CREATE OR REPLACE VIEW RestaurantMenuView AS\n" +      // can also be CREATE TEMPORARY VIEW
                "SELECT\n" +
                "    r.Name,\n" +
                "    m.MenuID,\n" +
                "    m.Last_updated,\n" +
                "    mi.Name AS ItemName,\n" +
                "    mi.Price\n" +
                "FROM Menu m\n" +
                "JOIN Restaurant r ON m.RestaurantID = r.RestaurantID\n" +
                "JOIN MenuItem mi ON m.MenuID = mi.MenuID;";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("View RestaurantMenuView created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CREATE VIEW RestaurantMenuView AS SELECT r.RestaurantName, m.MenuID, m.Last_updated, mi.Name AS ItemName, mi.Price FROM Menu m JOIN Restaurant r ON m.RestaurantID = r.RestaurantID JOIN MenuItem mi ON m.MenuID = mi.MenuID;
    // If we want to just add in on the MySQL side (I can't access it)

    public static Double getAverageRatingByRestaurant(int restaurantId) {
        Double result = null;

        String sql = "{ ? = CALL GetRestaurantAverageRating(?) }";

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.DECIMAL);
            stmt.setInt(2, restaurantId);
            stmt.execute();
            result = stmt.getDouble(1);

            if (stmt.wasNull()) {
                result = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        Connection con = getConnection();
        if (con == null){
            return;

                    // case 8:
                    //     createRestaurantMenuView();
                    //     break;
                    // case 9:                             //get average rating of a specific restaurant
                    //     System.out.println("Enter the Restaurant ID of the restaurant whose rating you want to see: ");
                    //     int restaurantId = input.nextInt();
                    //     getAverageRatingByRestaurant(restaurantId);
                    //     break;

        }


    }
}