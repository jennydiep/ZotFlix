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
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");

        System.out.println(title);
        System.out.println(year);
        System.out.println(director);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Create a new connection to database
            Connection dbcon = dataSource.getConnection();

            // Generate a SQL query
//            String query =  "SELECT * from movies where title like '%" + title +"%' ";
//            query += "and director like '%" + director + "%'";
//            if (!year.equals(""))
//            {
//                query += "and year = " + year;
//            }
//            String query = "drop view if exists movielist; ";
            String query = "";
//            create view movielist as
            query += " " +
                    "select title, year, director, starID, m.id, rating, numVotes, " +
                    "group_concat(distinct g.name) as \"genres\", " +
                    "group_concat(distinct genreId) as \"genreIDs\", " +
                    "group_concat(distinct s.name) as \"stars\", " +
                    "group_concat(distinct starId) as \"starIDs\" " +
                    "from movies as m, stars_in_movies as sm, stars as s, " +
                    "genres_in_movies as gm, genres as g, ratings as r " +
                    "where m.id = sm.movieId " +
                    "and sm.starId = s.id " +
                    "and gm.movieId = m.id " +
                    "and gm.genreId = g.id " +
                    "and r.movieId = m.id ";

            query +=  "and title like '%" + title +"%' ";
            query += "and director like '%" + director + "%' ";
            if (!year.equals(""))
            {
                query += "and year = " + year + " ";
            }
            query += "group by m.id;";



            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            ResultSet rs = statement.executeQuery();



            JsonArray jsonArray = new JsonArray();
            JsonObject jsonParamObject = new JsonObject();
            jsonParamObject.addProperty("title", title);
            jsonParamObject.addProperty("year", year);
            jsonParamObject.addProperty("director", director);

            jsonArray.add(jsonParamObject); // add search as first param in json array

            while (rs.next()) {
                String movie_id = rs.getString("ID");
                String movie_name = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_genres_id = rs.getString("genreids");
                String movie_stars = rs.getString("stars");
                String movie_stars_id = rs.getString("starids");
                String movie_rating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movie_id);
                jsonObject.addProperty("title", movie_name);
                jsonObject.addProperty("year", movie_year);
                jsonObject.addProperty("director", movie_director);
                jsonObject.addProperty("genres", movie_genres);
                jsonObject.addProperty("genres_id", movie_genres_id);
                jsonObject.addProperty("stars", movie_stars);
                jsonObject.addProperty("stars_id", movie_stars_id);
                jsonObject.addProperty("rating", movie_rating);

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
