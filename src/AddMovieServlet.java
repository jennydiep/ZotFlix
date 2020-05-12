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
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("starName");
        String title = request.getParameter("title");
        String movieYear = request.getParameter("movieYear");
        String director = request.getParameter("director");
        String genre = request.getParameter("genre");

        System.out.println("title: " + title);
        System.out.println("movieYear: " + movieYear);
        System.out.println("director: "+ director);
        System.out.println("starName: " + starName);
        System.out.println("genre: " + genre);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Declare our statement

            // getting max id to generate new id for star
            String query = "{CALL add_movie(?, ?, ?, ?, ?)}";
            CallableStatement statement = dbcon.prepareCall(query);
            statement.setString(1, title);
            statement.setInt(2, Integer.parseInt(movieYear));
            statement.setString(3, director);
            statement.setString(4, starName);
            statement.setString(5, genre);

            ResultSet rs = statement.executeQuery();


            JsonObject responseJsonObject = new JsonObject();

            if (rs.getString("movieId").equals("null")) // movie already exists
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "failed to add movie");
            }
            else // insert successful
            {
                String starId = rs.getString("starId");
                String movieId = rs.getString("movieId");
                String genreId = rs.getString("genreId");


                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                responseJsonObject.addProperty("starId", starId);
                responseJsonObject.addProperty("movieId", movieId);
                responseJsonObject.addProperty("genreId", genreId);
            }


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
