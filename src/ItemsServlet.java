import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This ItemsServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ItemsServlet", urlPatterns = "/api/items")
public class ItemsServlet extends HttpServlet {
    /**
     * handles POST requests to add and show the item list information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        HttpSession session = request.getSession();
        System.out.println("item: " + item);
        // get the previous items in a ArrayList
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        if (item != null) // if item is null ignore request
        {
                if (previousItems == null) {
                    previousItems = new ArrayList<>();
                    previousItems.add(item);
                    session.setAttribute("previousItems", previousItems);
                } else {
        // prevent corrupted states through sharing under multi-threads
        // will only be executed by one thread at a time
                    synchronized (previousItems) {
                        previousItems.add(item);
                    }
                }



//                response.getWriter().write(String.join(",", previousItems));
//            JsonObject responseJsonObject = new JsonObject();
//            responseJsonObject.addProperty("movieId", item);
//            // write all the data into the jsonObject
//            response.getWriter().write();
//


        }
        System.out.println(previousItems);
        response.getWriter().write(String.valueOf(previousItems));

    }
}