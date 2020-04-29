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
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String offset = request.getParameter("offset");
        String records = request.getParameter("records");
        String sortTitle = request.getParameter("sortTitle");
        String sortRating = request.getParameter("sortRating");

        System.out.println("title: " + title);
        System.out.println("year: " + year);
        System.out.println("director: " + director);
        System.out.println("star: " + star);
        System.out.println("genre: " + genre);
        System.out.println("records: " + records);
        System.out.println("offset: " + offset);
        System.out.println("sortTitle: " + sortTitle);
        System.out.println("sortRating: " + sortRating);


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Create a new connection to database
            Connection dbcon = dataSource.getConnection();

            // Generate a SQL query
            String queryViews1 = "create view advancedSearch as " +
                    "select title, m.id from stars_in_movies as sm, " +
                    "movies as m, stars as s, genres as g, genres_in_movies as gm ";

            queryViews1 +=
                    "where s.id = sm.starId " +
                    "and sm.movieId = m.id " +
                    "and gm.movieId = m.id " +
                    "and g.id = gm.genreId ";

            if (title.equals("*")) // to activate browse by non alphanumeric
            {
                queryViews1 += "and title NOT REGEXP '^[A-Za-z0-9\\.,@&\\(\\) \\-]*$'";
            }
            else if (!title.equals("")) // don't include title in query if it is not used
            {
                queryViews1 += "and title like '" + title + "%' ";
            }

            if (!year.equals(""))
            {
                queryViews1 += "and year = " + year + " ";
            }

            if (!director.equals(""))
            {
                queryViews1 += "and director like '%" + director + "%' ";
            }

            if (!star.equals(""))
            {
                queryViews1 += "and s.name like '%" + star + "%' ";
            }

            if (!genre.equals(""))
            {
                queryViews1 += "and g.id = " + genre + " ";
            }

            queryViews1 += "group by m.id";

            System.out.println(queryViews1);

            String queryViews2 =
                    "create view actors as  " +
                    "select distinct s.id as starId, name, m.id, title " +
                    "from advancedSearch as m, " +
                    "stars as s, stars_in_movies as sm " +
                    "where m.id = sm.movieId " +
                    "and sm.starId = s.id";

            String queryViews3 =
                    "create view popularity as  " +
                    "select starId, a.name, a.id, popularity from actors as a, topstars as ts " +
                    "where a.starId = ts.id " +
                    "order by popularity desc, a.name ";

            String queryViews4 =
                    "create view starsInMoviesSearch as " +
                    "select a.id, title, group_concat(name order by popularity DESC, name) as \"stars\",  " +
                    "group_concat(starId order by popularity DESC, name) as \"starIDs\"  " +
                    "from popularity as p join advancedSearch as a on p.id = a.id  " +
                    "group by id ";

            String queryViews5 =
                    "create view advGenreSearch as " +
                    "select ss.id, title, stars, starIDs, group_concat(g.name order by g.name) as \"genres\", group_concat(g.id order by g.name) as \"genreIDs\"  " +
                    "from starsInMoviesSearch as ss, genres as g, genres_in_movies as gm " +
                    "where g.id = gm.genreId " +
                    "and ss.id = gm.movieId " +
                    "group by ss.id ";

            String query =
                    "select m.id, m.title, director, stars, year, starIDs, genres, genreIDs, rating, numVotes " +
                    "from movies as m, advGenreSearch as ag left join ratings on ratings.movieId = ag.id " +
                    "where m.id = ag.id " +
                    "order by title " + sortTitle + ", rating " + sortRating + " " +
                    "limit " + records + " " +
                    "offset " + offset;
            // add order by title ASC, rating DESC and LIMIT 10,10

            // Declare our statement
            PreparedStatement statementDrops1 = dbcon.prepareStatement("drop view if exists advancedSearch");
            PreparedStatement statementDrops2 = dbcon.prepareStatement("drop view if exists actors");
            PreparedStatement statementDrops3 = dbcon.prepareStatement("drop view if exists popularity");
            PreparedStatement statementDrops4 = dbcon.prepareStatement("drop view if exists starsInMoviesSearch");
            PreparedStatement statementDrops5 = dbcon.prepareStatement("drop view if exists advGenreSearch");

            PreparedStatement statement = dbcon.prepareStatement(query);
            PreparedStatement statementViews1 = dbcon.prepareStatement(queryViews1);
            PreparedStatement statementViews2 = dbcon.prepareStatement(queryViews2);
            PreparedStatement statementViews3 = dbcon.prepareStatement(queryViews3);
            PreparedStatement statementViews4 = dbcon.prepareStatement(queryViews4);
            PreparedStatement statementViews5 = dbcon.prepareStatement(queryViews5);

            statementDrops1.execute();
            statementDrops2.execute();
            statementDrops3.execute();
            statementDrops4.execute();
            statementDrops5.execute();

            statementViews1.execute();
            statementViews2.execute();
            statementViews3.execute();
            statementViews4.execute();
            statementViews5.execute();

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
