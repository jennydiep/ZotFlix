import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * This ItemsServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "DecreaseItemServlet", urlPatterns = "/api/decreaseitem")
public class DecreaseItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        String item = request.getParameter("item");
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        System.out.println(previousItems);
        PrintWriter out = response.getWriter();

        for (int i = 0; i < previousItems.size(); i++)
        {
            JsonObject jsonObjectMovie = (JsonObject) previousItems.get(i);

            String temp = (jsonObjectMovie.get("movieId")).toString().replaceAll("\"", "");
            System.out.println("movie id: " + temp + " movieId: " + item);
            if ( item.equals(temp)) // go through each item and if found delete it
            {

                int quantity = Integer.parseInt(String.valueOf(((JsonObject) previousItems.get(i)).get("quantity")));
                quantity--;
                ((JsonObject) previousItems.get(i)).addProperty("quantity", quantity);
                System.out.println("decreased quantity");

            }
        }

    }
}

