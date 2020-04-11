## CS 122B Project 1: Fabflix


#### Demo Video: https://youtu.be/-4wtQdXwZ68
##### (Skip to 3:42 to skip populating db)
##### (Apologies for random stuff during populating the db I forgot how to start Tomcat)

##

### Before running the example

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

 - login to mysql as a root user 
    ```
    local> mysql -u root -p
    ```

 - create a test user and grant privileges:
    ```
    mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'mypassword';
    mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
    mysql> quit;
    ```

#### prepare the database `moviedb`
 

```
mysql -u mytestuser-p < create_table.sql
```
##

### To run this example on local machine: 
1. Clone this repository 
2. Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) -> Choose Import project from external model -> choose Maven -> Click on Finish -> The IntelliJ will load automatically
3. For "Root Directory", right click "cs122b-spring20-project1-api-example" -> Mark Directory as -> sources root
4. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `mypassword`.
5. Also make sure you have the `moviedb` database.
6. To run the example, follow the instructions in [canvas](https://canvas.eee.uci.edu/courses/26486/pages/intellij-idea-tomcat-configuration)

##

