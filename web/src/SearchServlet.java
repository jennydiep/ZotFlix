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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/fullsearch")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTS = System.nanoTime();
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String searchText = request.getParameter("search");
        searchText = (searchText==null) ? "" : searchText;
        System.out.println("searchText: " + searchText);

        long startTJ = System.nanoTime();
        long endTJ = 0; // temporary value
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select * from movies where match (title) against ( ? IN BOOLEAN MODE) limit 10";
            PreparedStatement statement = dbcon.prepareStatement(query);

            String[] temp = searchText.split(" ");
            String result = "";
            for (String word : temp)
            {
                result += " +" + word + "* ";
                System.out.println("fulltext query: " + result);
            }
            statement.setString(1,  result);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            endTJ = System.nanoTime();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", title);

                JsonObject additionalDataJsonObject = new JsonObject();
                additionalDataJsonObject.addProperty("id", id);
                jsonObject.add("data", additionalDataJsonObject);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            endTJ = System.nanoTime();
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
        long endTS = System.nanoTime();

        long elapsedTimeTJ = endTJ - startTJ; // elapsed time in nano seconds. Note: print the values in nano seconds
        long elapsedTimeTS = endTS - startTS;
        System.out.println(elapsedTimeTJ + "");
        System.out.println(elapsedTimeTS + "");

        String fileName = "TS.txt";
        String fileName2 = "TJ.txt";

        String contextPath = getServletContext().getRealPath("/");
        System.out.println(contextPath);
        fileHelper(fileName, contextPath, elapsedTimeTS);
        fileHelper(fileName2, contextPath, elapsedTimeTJ);

    }

    void fileHelper(String fileName, String contextPath, long time)
    {
        try {
            FileWriter fileWriter = new FileWriter(contextPath + "\\" + fileName, true);

            System.out.println(contextPath + "\\" + fileName);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            printWriter.println(time + "");
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}


