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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query1 = "select distinct s.name, s.id, m.title, m.year, m.director " +
                    "from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = ? " +
                    "and sim.starId = s.id " +
                    "and sim.movieId = m.id ";

            String query2 = "select distinct g.name, g.id " +
                    "from movies as m, genres_in_movies as gm, genres as g " +
                    "where m.id = ? " +
                    "and gm.movieId = m.id " +
                    "and gm.genreId = g.id";

            // gets rating if empty table = no rating
            String query3 = "select distinct r.rating from ratings as r, movies as m where m.id = ? and m.id = r.movieId ";

//            String query3 = "select * from movies as m where m.id = ?"

            // Declare our statement
            PreparedStatement statement1 = dbcon.prepareStatement(query1);
            PreparedStatement statement2 = dbcon.prepareStatement(query2);
            PreparedStatement statement3 = dbcon.prepareStatement(query3);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement1.setString(1, id);
            statement2.setString(1, id);
            statement3.setString(1, id);

            // Perform the query
            ResultSet rs1 = statement1.executeQuery();

            JsonArray jsonArray = new JsonArray();

            JsonObject jsonObjectStars = new JsonObject();
            JsonArray jsonArrayStars = new JsonArray();

            // Iterate through each row of rs
            while (rs1.next()) {

                String starId = rs1.getString("id");
                String starName = rs1.getString("name");
                String title = rs1.getString("title");
                String year = rs1.getString("year");
                String director = rs1.getString("director");


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);


                jsonArrayStars.add(jsonObject);
            }

            jsonArray.add(jsonArrayStars);

            ResultSet rs2 = statement2.executeQuery();
            JsonArray jsonArrayGenres = new JsonArray();

            while (rs2.next()) {

                String genreId = rs2.getString("id");
                String genreName = rs2.getString("name");


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_id", genreId);
                jsonObject.addProperty("genre_name", genreName);


                jsonArrayGenres.add(jsonObject);
            }

            jsonArray.add(jsonArrayGenres);

            ResultSet rs3 = statement3.executeQuery();

            while (rs3.next())
            {
                String rating = rs3.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }


            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs1.close();
            rs2.close();
            rs3.close();
            statement1.close();
            statement2.close();
            statement3.close();
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
