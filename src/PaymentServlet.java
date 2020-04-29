import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called PaymentServlet, which maps to url "/api/stars"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccId = request.getParameter("ccId");
        String expDate = request.getParameter("expDate");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT * from creditcards where id =" + ccId +
                    " and firstName = " + firstName +
                    " and lastName = " + lastName +
                    " and expiration = " + expDate;

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

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
                responseJsonObject.addProperty("status", "success");
            }
            else
            {
                responseJsonObject.addProperty("status", "failed");
            }

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
}
