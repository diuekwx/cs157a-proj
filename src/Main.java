import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static void createRestaurantTransaction(Connection conn, String name, String address, int[] categoriesIds) throws SQLException {
        // maybe add checks for each execute?
        try {
            conn.setAutoCommit(false);
            String insertRestaurant = "INSERT INTO Restaurant (Name, Address) VALUES (?, ?)";
            String insertCategories = "INSERT INTO RestaurantCategory (RestaurantID, CategoryID) VALUES (?, ?)";
            String insertMenu = "INSERT INTO Menu (RestaurantID, Last_updated) VALUES (?, NOW())";

            int createdRestaurantId = -1;
            // need 2nd param to get key for other queries
            try (PreparedStatement ps = conn.prepareStatement(insertRestaurant, Statement.RETURN_GENERATED_KEYS);) {
                ps.setString(1, name);
                ps.setString(2, address);
                ps.executeUpdate();

                // seperate check for key gen, just clarity purposes
                try (ResultSet generatedKey = ps.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        createdRestaurantId = generatedKey.getInt(1);
                    } else {
                        throw new SQLException("Creating restaurant failed, no new key");
                    }
                }
                System.out.printf("Created new restaurant with ID: %d \n", createdRestaurantId);
            }

            try (PreparedStatement ps = conn.prepareStatement(insertCategories)) {
                for (int i = 0; i < categoriesIds.length; i++) {
                    ps.setInt(1, createdRestaurantId);
                    ps.setInt(2, categoriesIds[i]);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("\nCategories added to new restaurant \n");
            }

            try (PreparedStatement ps = conn.prepareStatement(insertMenu)) {
                ps.setInt(1, createdRestaurantId);
                ps.executeUpdate();
                System.out.println("Associated menu for restaurant created \n");
            }

            conn.commit();
            System.out.println("Creation of restaurant and associated categories and menu completed! \n");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Transaction failed, rolling back: " + e.getMessage());
        }
    }

    private static boolean isValidUserID(Connection conn, int userID) {
        String sql = "SELECT 1 FROM Person WHERE UserID = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();   // true if a row exists
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private static int scanID(Connection conn, Scanner scan) {
        int userID = -1;
        while (userID < 0) {
            try {
                userID = scan.nextInt();
                scan.nextLine();
                if (userID < 0) {
                    System.out.println("ID must be non-negative, try again.");
                    continue;
                }
                if (!isValidUserID(conn, userID)) {
                    System.out.println("This user does not exist, try again.");
                    userID = -1;
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Error: please enter a valid number.");
                scan.nextLine();
            }
        }
        return userID;
    }

    private static String scanNonemptyString(Scanner scan, String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scan.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be blank, please try again.");
        }
    }

    private static void printResultSet(ResultSet rs, String tableName) throws SQLException {
        System.out.println("=============== " + tableName +  " ===============\n");
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCols = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= numCols; i++) {
                System.out.println(rsmd.getColumnName(i) + ": " + rs.getString(i));
            }
            System.out.println();
        }
        System.out.println("=============== " + tableName +  " ===============\n");
    }

    private static void selectTable(Connection conn, Scanner input) {
        try {
            System.out.println("\nChoose the number of the table to select from (type \"6\" to exit):");
            String prompt = """
                    1. Restaurant
                    2. RestaurantCategory
                    3. Menu
                    4. Person
                    5. Category
                    6. Exit
                    """;
            System.out.println(prompt);
            String table = "";
            int choice = 0;
            while (!input.hasNext("[1-6]")) {
                System.out.println("Invalid input. Please try again: ");
                System.out.println(prompt);
                input.next();
            }
            choice = input.nextInt();
            input.nextLine();
            switch (choice) {
                case 1:
                    table = "Restaurant";
                    break;
                case 2:
                    table = "RestaurantCategory";
                    break;
                case 3:
                    table = "Menu";
                    break;
                case 4:
                    table = "Person";
                    break;
                case 5:
                    table = "Category";
                    break;
                case 6:
                    System.out.println();
                    return;
                default:
                    System.out.println("Invalid choice, returning.");
                    return;
            }
            /* NOTE: PreparedStatement cannot be used to set table name
            However, since table name is not set directly by user input,
            we are safe from SQL injection since table can only take from
            5 values: "Restaurant", "RestaurantCategory", "Menu", "Person", "Category"
             */
            String selectSQL = String.format("SELECT * FROM %s", table);
            PreparedStatement ps = conn.prepareStatement(selectSQL);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs, table);
        } catch (SQLException e) {
            System.out.println("A database error occurred while selecting table:");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void insertPerson(Connection conn, Scanner input) {
        try {
            String name = scanNonemptyString(input, "Enter a name:");
            String email = scanNonemptyString(input, "Enter an email address:");

            String insertSQL = "INSERT INTO Person (Name, Email) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setString(1, name);
            ps.setString(2, email);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Insert failed: no rows were added.");
            } else {
                System.out.println("New person " + name + " successfully inserted.\n");
            }

        }

        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Insert failed due to a data constraint violation.");
            System.out.println("Reason: " + e.getMessage());
        }

        catch (SQLSyntaxErrorException e) {
            System.out.println("SQL syntax error while inserting.");
            System.out.println("Reason: " + e.getMessage());
        }

        catch (SQLException e) {
            System.out.println("A database error occurred while inserting the person:");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updatePerson(Connection conn, Scanner input) {
        try {
            System.out.println("Enter the UserID:");
            int userID = scanID(conn, input);

            String name = scanNonemptyString(input, "Enter a new name:");
            String email = scanNonemptyString(input, "Enter a new email address:");

            String insertSQL = "UPDATE Person SET name = ?, email = ? WHERE UserID = ?";
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, userID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No record found with UserID: " + userID);
            } else {
                System.out.println("Person with ID " + userID + " updated successfully (" + rowsAffected + " row(s) affected).");
            }

        }

        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Update failed due to a data constraint violation.");
            System.out.println("Reason: " + e.getMessage());
        }

        catch (SQLSyntaxErrorException e) {
            System.out.println("SQL syntax error while updating.");
            System.out.println("Reason: " + e.getMessage());
        }

        catch (SQLException e) {
            System.out.println("A database error occurred while updating the person");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deletePerson(Connection conn, Scanner input) {
        try {
            System.out.println("Enter userID:");
            int userID = scanID(conn, input);
            String insertSQL = "DELETE FROM Person WHERE UserID = ?";

            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setInt(1, userID); 
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No record found with UserID: " + userID);
            } else {
                System.out.println("Person with ID " + userID + " deleted successfully (" + rowsAffected + " row(s) deleted).");
            }
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Delete failed due to a data constraint violation.");
            System.out.println("Reason: " + e.getMessage());
        }
        catch (SQLSyntaxErrorException e) {
            System.out.println("SQL syntax error while trying to delete the person.");
            System.out.println("Reason: " + e.getMessage());
        }
        catch (SQLException e) {
            System.out.println("A database error occurred while deleting the person.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Connection getConnection() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("app.properties")) {
            p.load(in);

            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Connection con = null;
        try{
            con = getConnection();
            System.out.println("Connected to " + con.getCatalog());
        }
        catch (Exception e){
            System.out.println("Could not find a connection.");
            return;
        }

        String menuPrompt = """
                1. Select Table
                2. Insert Person
                3. Update Person
                4. Delete Person
                5. Run Transaction
                6. Exit
                """;

        while (true) {
            System.out.println(menuPrompt);
            System.out.println("Please enter the # of your desired action: ");
            Scanner input = new Scanner(System.in);
            if (input.hasNext("[1-7]")) {
                int choice = input.nextInt();
                input.nextLine();
                switch (choice) {
                    case 1:
                        selectTable(con, input);
                        break;
                    case 2:
                        insertPerson(con, input);
                        break;
                    case 3:
                        updatePerson(con, input);
                        break;
                    case 4:
                        deletePerson(con, input);
                        break;
                    case 5:
                        String name = scanNonemptyString(input, "Please enter the name of the new restaurant:");
                        String address = scanNonemptyString(input, "Please enter the address of the new restaurant:");

                        while (address.trim().isEmpty()) {
                            System.out.println("Please enter the address of the new restaurant: ");
                            address = input.nextLine();
                            if (address.trim().isEmpty()) {
                                System.out.println("Address cannot be empty. Please try again.\n");
                            }
                        }
                        int[] nums = null;
                        boolean validInput = false;
                        while (!validInput) {
                            System.out.println("Please enter the ID of the restaurant's categories seperated by spaces(e.g. 1 2 3 ...): ");
                            String categoryIDs = input.nextLine();

                            try {
                                String[] parts = categoryIDs.split(" ");
                                nums = new int[parts.length];
                                for (int i = 0; i < nums.length; i++) {
                                    nums[i] = Integer.parseInt(parts[i]);
                                }
                                validInput = true;
                            } catch (NumberFormatException e) {
                                System.out.println();
                                System.out.println("Invalid category ID format. Please enter only integers separated by spaces.");
                                System.out.println();
                            }
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
            } else {
                System.out.println("Invalid input");
                input.nextLine();
            }
        }
    }
}