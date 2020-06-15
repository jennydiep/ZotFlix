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


// Declaring a WebServlet called PaymentServlet, which maps to url "/api/stars"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccId = request.getParameter("ccId");
        String expDate = request.getParameter("expDate");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "Select * from creditcards where id = ? " +
                    " and firstName = ?" +
                    " and lastName = ? " +
                    " and expiration = ? ";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, ccId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expDate);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
//            while (rs.next()) {
//                String id = rs.getString("id");
//
//
//                // Create a JsonObject based on the data we retrieve from rs
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("id", id);
//
//
//                jsonArray.add(jsonObject);
//            }
            JsonObject responseJsonObject = new JsonObject();
            if (rs.next() == false)
            {
                //                JsonObject jsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "failed");
            }
            else
            {
                responseJsonObject.addProperty("status", "success");
            }

            jsonArray.add(responseJsonObject);

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
