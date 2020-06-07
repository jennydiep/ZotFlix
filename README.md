## CS 122B: Fabflix

- # General
    - #### Team#: 82
    
    - #### Names: Jenny Diep
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:
        ##### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:
    
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
    
        - prepare the database `moviedb`
            ```
            mysql -u mytestuser-p < create_table.sql
            ```
    
        #### To run this example on local machine: 
            1. Clone this repository 
            2. Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) -> Choose Import project from external model -> choose Maven -> Click on Finish -> The IntelliJ will load automatically
            3. For "Root Directory", right click "cs122b-spring20-project1-api-example" -> Mark Directory as -> sources root
            4. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `mypassword`.
            5. Also make sure you have the `moviedb` database.
            6. To run the example, follow the instructions in [canvas](https://canvas.eee.uci.edu/courses/26486/pages/intellij-idea-tomcat-configuration)

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        #### 
    
       [context.xml](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/WebContent/META-INF/context.xml)
    
       [DashBoardServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/DashBoardServlet.java)
       
       [LoginServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/LoginServlet.java)
       
       [ItemsServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/ItemsServlet.java)
       
       [PaymentServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/PaymentServlet.java)
       
       [AdvancedSearchServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/AdvancedSearchServlet.java)
       
       [SingleMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SingleMovieServlet.java)
       
       [SingleStarServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SingleStarServlet.java)
       
       [AddMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/AddMovieServlet.java)
       
       [GenreServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/GenreServlet.java)
       
       [SearchServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SearchServlet.java)
                             
       [MetaDataServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/MetaDataServlet.java)
                               
       [StarsServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/StarsServlet.java)
                             
        ####
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
          maxTotal="100" maxIdle="30" maxWaitMillis="10000"
        maxTotal represents the max amount of connections to the db which is 100 connects, maxIdle represents the minimum number
        of connections and maxWaitMillis represents the amount of time waiting on the connection before timing out.
        
        Since there are many calls to the db in Fabflix connection pooling is a way to make calls from and to the database
        more efficient. Instead of establishing new connections for example in each search call, instead it will
        use a connection that was already established in the pool of connections.
    
    - #### Explain how Connection Pooling works with two backend SQL.
        Connection pooling with two backend databases works similar to single database where instead of establishing 
        new connections it chooses existing connections, however the pools are shared between two databases which
        speeds up queries since there can be multiple queries at once instead of waiting for one query to finish.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    
    - config file: [context.xml](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/WebContent/META-INF/context.xml)
    
    - master only:  
    [AddMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/AddMovieServlet.java)  
    [DashBoardServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/DashBoardServlet.java)
    - master/slave:  
    [LoginServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/LoginServlet.java)  
    [ItemsServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/ItemsServlet.java)  
    [PaymentServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/PaymentServlet.java)  
    [AdvancedSearchServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/AdvancedSearchServlet.java)  
    [SingleMovieServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SingleMovieServlet.java)  
    [SingleStarServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SingleStarServlet.java)  
    [GenreServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/GenreServlet.java)  
    [SearchServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/SearchServlet.java)  
    [MetaDataServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/MetaDataServlet.java)  
    [StarsServlet](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-82/blob/master/web/src/StarsServlet.java)  

    - #### How read/write requests were routed to Master/Slave SQL?
        Read request chooses SQL instance that is linked to its session. The SQL instance that is chosen by the session is
        picked at random (either the slave or master instance, whatever is localhost) while write requests are always from 
        the master instance.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
        log_processing.py prints out the average TS and TJ time
        
        Go to set folder ex: .../log/1
        
        Run:
          
          python3 ../log_processing.py       
        

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

##
    
### substring matching 
    title: any title that starts with "x"
    director: has "x" in it
    star: has "x" in it



