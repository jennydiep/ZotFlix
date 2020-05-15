## CS 122B: Fabflix

### Web Application URL: http://ec2-3-18-107-131.us-east-2.compute.amazonaws.com:8080/cs122b-spring20/
### Demo Video: https://www.youtube.com/watch?v=3Ifj7hisXYQ

### (Did not finish task 7)
##

#### Files that contain prepared statements:

   [DashBoardServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/DashBoardServlet.java)
   
   [LoginServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/LoginServlet.java)
   
   [ItemsServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/ItemsServlet.java)
   
   [PaymentServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/PaymentServlet.java)
   
   [AdvancedSearchServlet (sorting does not use '?' parameters but still prevents SQL Injection)](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/SearchServlet.java)
   
   [SingleMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/SingleMovieServlet.java)
   
   [SingleStarServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/SingleStarServlet.java)
   
   [AddMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/src/AddMovieServlet.java)
    
##
    
### substring matching 
    title: any title that starts with "x"
    director: has "x" in it
    star: has "x" in it


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

