import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static void createRestaurantTransaction(Connection conn, String name, String address, int[] categoriesIds) throws SQLException{
        // maybe add checks for each execute?
        try{
            conn.setAutoCommit(false);
            String insertRestaurant = "INSERT INTO Restaurant (Name, Address) VALUES (?, ?)";
            String insertCategories = "INSERT INTO RestaurantCategory (RestaurantID, CategoryID) VALUES (?, ?)";
            String insertMenu = "INSERT INTO Menu (RestaurantID, Last_updated) VALUES (?, NOW())";

            int createdRestaurantId = -1;
            // need 2nd param to get key for other queries
            try(PreparedStatement ps = conn.prepareStatement(insertRestaurant, Statement.RETURN_GENERATED_KEYS);){
                ps.setString(1, name);
                ps.setString(2, address);
                ps.executeUpdate();

                // seperate check for key gen, just clarity purposes
                try (ResultSet generatedKey = ps.getGeneratedKeys()){
                    if (generatedKey.next()){
                        createdRestaurantId = generatedKey.getInt(1);
                    }
                    else{
                        throw new SQLException("Creating restaurant failed, no new key");
                    }
                }
                System.out.println("Created new restaurant");
            }

            try(PreparedStatement ps = conn.prepareStatement(insertCategories)){
                for (int i = 0; i < categoriesIds.length; i++){
                    ps.setInt(1, createdRestaurantId);
                    ps.setInt(2, categoriesIds[i]);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("Categories added to new restaurant");
            }

            try(PreparedStatement ps = conn.prepareStatement(insertMenu)){
                ps.setInt(1, createdRestaurantId);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Creation of restaurant and associated categories and menu completed!");
        }
        catch (SQLException e){
            conn.rollback();
            System.out.println("Transaction failed, rolling back: " + e.getMessage());
        }
    }

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

    public static void main(String[] args) throws Exception {
        Connection con = getConnection();
        if (con == null){
            return;
        }
        System.out.println("Connected to " + con.getCatalog());

        String menuPrompt = """
                1. View Data
                2. Insert Data
                3. Update Data
                4. Delete Data
                5. Run Transaction
                6. Exit
                """;

        while(true){
            System.out.println(menuPrompt);
            System.out.println("Please enter the # of your desired action: ");
            Scanner input = new Scanner(System.in);
            if (input.hasNext("[1-6]")){
                int choice = input.nextInt();
                input.nextLine();
                switch (choice) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        System.out.println("Please enter the name of the new restaurant: ");
                        String name = input.nextLine();

                        System.out.println("Please enter the address of the new restaurant: ");
                        String address = input.nextLine();

                        System.out.println("Please enter the ID of the restaurant's categories seperated by spaces(e.g. 1 2 3 ...): ");
                        String categoryIDs = input.nextLine();

                        String[] parts = categoryIDs.split(" ");
                        int[] nums = new int[parts.length];
                        for (int i = 0; i < nums.length; i++){
                            nums[i] = Integer.parseInt(parts[i]);
                        }

                        createRestaurantTransaction(con, name, address, nums);
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        input.close();
                        con.close();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
            else{
                System.out.println("Invalid input");
                input.nextLine();
            }

        }


    }
}