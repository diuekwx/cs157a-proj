Text-based GUI for a Restaurant Review App

To Run:
-Ensure MySQL Server is installed
-Have MySQL Client ('mysql') available on PATH (verify by running mysql --version)

-Navigate to project directory
-Initialize the database by running 
``` 
mysql -u root -p < sql/create_db.sql
```
-Then populate the database by running
```
mysql -u root -p < sql/create_and_populate.sql
```
-Ensure that the app.properties file is properly configured to match user credentials (change user and password)

-Ensure a working JDK is installed and added to PATH
-Compile the program by running:
```
javac -cp ".;lib\mysql-connector-j-9.5.0.jar" src\Main.java
```
-Run the file by running:
```
java -cp ".;lib\mysql-connector-j-9.5.0.jar;src" Main
```