import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * This ItemsServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ItemsServlet", urlPatterns = "/api/items")
public class ItemsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String item = request.getParameter("item");
//        HttpSession session = request.getSession();
//        System.out.println("item: " + item);
//        // get the previous items in a ArrayList
//        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
//
//        if (item != null) // if item is null ignore request
//        {
//                if (previousItems == null) {
//                    previousItems = new ArrayList<>();
//                    previousItems.add(item);
//                    session.setAttribute("previousItems", previousItems);
//                } else {
//        // prevent corrupted states through sharing under multi-threads
//        // will only be executed by one thread at a time
//                    synchronized (previousItems) {
//                        previousItems.add(item);
//                    }
//                }
//        }
//        ArrayList<String> cartList = previousItems;
//        JsonArray jsonArray= new JsonArray();
//        for (int i = 0; i < cartList.size(); i++)
//        {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("movieId", cartList.get(i));
//            jsonArray.add(jsonObject);
//
//        }
//
//        System.out.println(previousItems);
//        response.getWriter().write(jsonArray.toString());

        response.setContentType("application/json"); // Response mime type
        String item = request.getParameter("item");
        HttpSession session = request.getSession();
        System.out.println("item: " + item);
        // get the previous items in a ArrayList
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        PrintWriter out = response.getWriter();

        try {

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "SELECT * from movies where id = '" + item +"'";
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            rs.next();

            if (item != null) // if item is null ignore request
            {


                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("movieTitle", movieTitle);

                if (previousItems == null) {
                    previousItems = new JsonArray();
                    previousItems.add(jsonObject);
                    session.setAttribute("previousItems", previousItems);
                } else {
                    // prevent corrupted states through sharing under multi-threads
                    // will only be executed by one thread at a time
                    synchronized (previousItems) {
                        previousItems.add(jsonObject);
                    }
                }

            }

            // write JSON string to output
            out.write(previousItems.toString());
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