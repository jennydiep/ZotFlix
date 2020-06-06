import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "DashBoardServlet", urlPatterns = "/api/dashboard")
public class DashBoardServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("starName");
        String starYear = request.getParameter("starYear");

        System.out.println("starName: " + starName);
        System.out.println("starYear: " + starYear);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/master");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Declare our statement

            // getting max id to generate new id for star
            String queryId = "select max(id) from stars";
            PreparedStatement statementId = dbcon.prepareStatement(queryId);
            ResultSet rsId = statementId.executeQuery();
            String starId = "nm9423081"; // default id

            if (rsId.next() != false)
            {
                String sId = rsId.getString("max(id)");
                int tempNum = Integer.parseInt(sId.substring(2)); // gets just the int of the id
                tempNum++;
                System.out.print(tempNum);

                starId = "nm" + tempNum;
            }

            rsId.close();

            // inserting star
            String query = "";

            boolean yearInParameter = false;

            if (starYear.equals("")) // year doesn't exist
            {
                query = "INSERT INTO stars (id, name) VALUES(?, ?)";
            }
            else
            {
                query = "INSERT INTO stars (id, name, birthYear) VALUES(?, ?, ?)";
                yearInParameter = true;
            }


            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, starId);
            statement.setString(2, starName);
            if (yearInParameter)
            {
                statement.setInt(3, Integer.parseInt(starYear));
            }

            // Perform the query
            int insert = statement.executeUpdate();
            JsonObject responseJsonObject = new JsonObject();

            if (insert != 0) // insert successful
            {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                responseJsonObject.addProperty("starId", starId);
            }
            else // insert did not effect tables = fail
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "failed to add star");
            }


            // write JSON string to output
            out.write(responseJsonObject.toString());

            // set response status to 200 (OK)
            response.setStatus(200);

            statement.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
