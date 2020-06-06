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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

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
            // Declare our statement
            String query = "SELECT email, password from customers where email = ?";
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, username);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            String queryEmp = "SELECT email, password from employees where email = ?";
            PreparedStatement statementEmp = dbcon.prepareStatement(queryEmp);
            statementEmp.setString(1, username);
            ResultSet rsEmp = statementEmp.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            VerifyPassword verify = new VerifyPassword();

            if (rs.next() != false) {
//                System.out.println(verify.verifyCredentials(username, password));
                if (verify.verifyCredentials(username, password)) {
                    // Login success:

                    // set this user into the session
                    request.getSession().setAttribute("user", new User(username, false));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else // Login failed
                {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password/email");

                }
            }
            else if (rsEmp.next() != false) // check employees
            {
                if (password.equals(rsEmp.getString("password")))
                {
                    request.getSession().setAttribute("user", new User(username, true));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
            }
            else
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "email does not exist");
            }

//            try {
//                // recaptcha response
//                String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//                System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
//                // Vertify reCAPTCHA
//                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//            } catch (Exception e) {
//                responseJsonObject.addProperty("status", "fail");
//                responseJsonObject.addProperty("message", "reCaptcha failed");
//            }

            // write JSON string to output
            out.write(responseJsonObject.toString());

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

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
