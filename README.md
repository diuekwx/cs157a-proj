Text-based GUI for a Restaurant Review App

To Run:

-Ensure MySQL Server is installed
-Have MySQL Client ('mysql') available on PATH (verify by running mysql --version)

-Navigate to project directory

-Initialize the database by running:
``` 
mysql -u root -p < sql/create_db.sql
```
-Then populate the database by running:
```
mysql -u root -p < sql/create_and_populate.sql
```
-Ensure that the app.properties file is properly configured to match user credentials (change user and password),   -This project assumes the default configurations of the database:
    -Host: Localhost
    -Port: 3306
    -If your MySQL server uses a different host or port, you must also update the url in app.properties

-Ensure a working JDK is installed and added to PATH

-Compile the program by running:
```
javac -cp ".;lib\mysql-connector-j-9.5.0.jar" src\Main.java
```
-Run the file by running:
```
java -cp ".;lib\mysql-connector-j-9.5.0.jar;src" Main
```

Application Information:
The application was built iteratively and combining all the required steps. The first step was to ensure a proper connection could be made by adding the JDBC Driver. After the connection was tested, the simple text-based menu was made with all the required components. After defining the menu, all that was left was to complete the methods that would accompany each case based on the menu options. After that was completed, validation and error handling were added to communicate any potential violations. 


MySQL Version: 9.4.0

Connector Info (JDBC Driver): Included inside /lib/mysql-connector-j-9.5.0
