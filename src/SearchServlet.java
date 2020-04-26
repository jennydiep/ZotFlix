import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/search"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
        String name = request.getParameter("title");
        String year = request.getParameter("year");
//        String director = request.getParameter("director");

        System.out.println(name);
        System.out.println(year);


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

            // Create a new connection to database
            Connection dbcon = dataSource.getConnection();

            // Generate a SQL query
            String query =  "SELECT * from stars where name like '%" + name +"%' ";
            if (!year.equals(""))
            {
                query += "and birthYear " + year;
            }

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);


            // Perform the query
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonParamObject = new JsonObject();
            jsonParamObject.addProperty("name", name);
            jsonParamObject.addProperty("year", year);

            jsonArray.add(jsonParamObject); // add search as first param in json array

            while (rs.next()) {
                String m_ID = rs.getString("ID");
                String m_Name = rs.getString("name");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", m_ID);
                jsonObject.addProperty("star_name", m_Name);

                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            // Close all structures
            rs.close();
            statement.close();
            dbcon.close();

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
