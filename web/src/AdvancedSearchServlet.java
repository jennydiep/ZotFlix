import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/search"
@WebServlet(name = "AdvancedSearchServlet", urlPatterns = "/api/search")
public class AdvancedSearchServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long startTS = System.nanoTime();

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

//        String sort = request.getParameter("sort");
//        String option = request.getParameter("option");
//        String option2 = request.getParameter("option2");

        title = (title==null) ? "" : title;
        year = (year==null) ? "" : year;
        director = (director==null) ? "" : director;
        star = (star==null) ? "" : star;
        genre = (genre==null) ? "" : genre;
        offset = (offset==null) ? "0" : offset;
        records = (records==null) ? "10" : records;
        sortTitle = (sortTitle==null) ? "ASC" : sortTitle;
        sortRating = (sortRating==null) ? "DESC" : sortRating;
//        sort = (sort==null) ? "title" : sort;
//        option = (option==null) ? "ASC" : option;
//        option2 = (option2==null) ? "ASC" : option2;

        ArrayList<String> elements = new ArrayList<String>();
        elements.add(star);
        elements.add(year);
        elements.add(director);
        elements.add(star);
        elements.add(genre);



        System.out.println("title: " + title);
        System.out.println("year: " + year);
        System.out.println("director: " + director);
        System.out.println("star: " + star);
        System.out.println("genre: " + genre);
        System.out.println("records: " + records);
        System.out.println("offset: " + offset);
        System.out.println("sortRating: " + sortRating);
        System.out.println("sortTitle: " + sortTitle);


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        long startTJ = System.nanoTime();
        long endTJ;
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Create a new connection to database
            Connection dbcon = dataSource.getConnection();

            // creating arraylist to store which parameters are used
            // for dynamic query, 1 means it is used, 0 means it is not used.
            ArrayList<Integer> parameters = new ArrayList<Integer>();
            for (int i = 0; i < 5; i++)
            {
                parameters.add(0);
            }

            // Generate a SQL query
            String query = "select m.id, title, year, director, rating, numVotes, " +
                    "group_concat(distinct s.id, \" \", s.name order by popularity desc) as stars, " +
                    "group_concat(distinct g.name) as genres, group_concat( distinct g.id) as genreIds " +
                    "from movies as m left join ratings as r on m.id = r.movieId, " +
                    "stars as s join topstars as ts on s.id = ts.id , " +
                    "genres as g, genres_in_movies as gm, stars_in_movies as sm " +
                    "where sm.starId = s.id " +
                    "and sm.movieId = m.id " +
                    "and m.id = gm.movieId " +
                    "and g.id = gm.genreId ";

            if (title.equals("*")) // to activate browse by non alphanumeric
            {
                query += "and title NOT REGEXP '^[A-Za-z0-9\\.,@&\\(\\) \\-]*$' ";
            }
            else if (!title.equals("")) // don't include title in query if it is not used
            {
                if (title.length() == 1)
                // for searching through alphabet
                { query += "and title like ? "; parameters.set(0, 1); }
                // else use full text search
//                else { query += "and match (title) against ( ? IN BOOLEAN MODE) "; parameters.set(0, 1); }
                // revert to using like for project 2 to work
                else { query += "and title like ? "; parameters.set(0, 1); }


            }

            if (!year.equals(""))
            { query += "and year = ? "; parameters.set(1, 1); }

            if (!director.equals(""))
            { query += "and director like ? "; parameters.set(2, 1); }

            if (!star.equals(""))
            { query += "and s.name like ? "; parameters.set(3, 1); }

            if (!genre.equals(""))
            { query += "and g.id = ? "; parameters.set(4, 1); }

            query += "group by m.id ";

            if ((sortRating.equals("ASC") || sortRating.equals("DESC")) && ((sortTitle.equals("ASC") || sortTitle.equals("DESC")) ))
            {
                query += "order by rating " + sortRating + ", title " + sortTitle + " ";
            }

//            if ((option.equals("ASC") || option.equals("DESC")) && ((option2.equals("ASC") || option2.equals("DESC")))) { // to prevent sql injection
//                // setting sort by option
//                if (sort.equals("title")) {
//                    query += "order by rating  "  + rating + ", title " + title + " ";
//                } else if (sort.equals("rating")) {
//                    query += "order by rating "  + option + ", title " + option2 + " ";
//                }
//            }

            query += "limit ? ";
            query += "offset ? ";

            System.out.println(query);


            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            System.out.println("Parameters: " + parameters.toString());

            int index = 1;
            for (int i = 0; i < 5; i++)
            {
                if (parameters.get(i).equals(1)) {
                    System.out.println("parameters.get(i)  " + parameters.get(i).toString() );
                    if (i == 1 || i == 4) { // year or genre
                        int temp = Integer.parseInt(elements.get(i));
                        System.out.println("index: " + index);
                        statement.setInt(index, temp);
                        index++;
                    } else if (i == 0) // title
                    {
                        if (title.length() == 1) // if it's one character long search by x%
                        {
                            statement.setString(index, title + "%");
                            index++;
                        } else // using full text search
                        {
                            // commenting out full text search to revert to project 2
//                            String[] temp = title.split(" ");
//                            String result = "";
//                            for (String word : temp)
//                            {
//                                result += " +" + word + "* ";
//                                System.out.println("fulltext query: " + result);
//                            }
//                            statement.setString(index,  result);
//                            index++;
                            statement.setString(index, "%" + title + "%");
                            index++;
                        }
                    }
                    else
                    {
                        statement.setString(index, "%" + elements.get(i) + "%");
                        index++;
                    }
                }
            }

            // set records and offset
            statement.setInt(index, Integer.parseInt(records));
            index++;
            statement.setInt(index, Integer.parseInt(offset));

            System.out.println(statement.toString());

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
//                String movie_stars_id = rs.getString("starids");
                String movie_rating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movie_id);
                jsonObject.addProperty("title", movie_name);
                jsonObject.addProperty("year", movie_year);
                jsonObject.addProperty("director", movie_director);
                jsonObject.addProperty("genres", movie_genres);
                jsonObject.addProperty("genres_id", movie_genres_id);
                jsonObject.addProperty("stars", movie_stars);
//                jsonObject.addProperty("stars_id", movie_stars_id);
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
            endTJ = System.nanoTime();

            // to save to session to return to movie list from single movie page
            String queryString = request.getQueryString();
            request.getSession().setAttribute("movielist", "search.html?"  + queryString);

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            endTJ = System.nanoTime();
            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
        long endTS = System.nanoTime();

        long elapsedTimeTJ = endTJ - startTJ; // elapsed time in nano seconds. Note: print the values in nano seconds
        long elapsedTimeTS = endTS - startTS;
//        System.out.println(elapsedTimeTJ + "");
//        System.out.println(elapsedTimeTS + "");

        String fileName = "TS.txt";
        String fileName2 = "TJ.txt";

        String contextPath = getServletContext().getRealPath("/");
//        System.out.println(contextPath);
//        fileHelper(fileName, contextPath, elapsedTimeTS);
//        fileHelper(fileName2, contextPath, elapsedTimeTJ);
    }

    void fileHelper(String fileName, String contextPath, long time)
    {
        try {
            FileWriter fileWriter = new FileWriter(contextPath + "/" + fileName, true);

            System.out.println(contextPath + "/" + fileName);

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
