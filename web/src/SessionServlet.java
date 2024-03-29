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
 * This SessionServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "SessionServlet", urlPatterns = "/api/session")
public class SessionServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();
        User currentUser = (User) session.getAttribute("user");

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("currentUser", currentUser.getUsername());
        responseJsonObject.addProperty("admin", currentUser.getAdmin());
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        if (session.getAttribute("previousItems") == null) {
            responseJsonObject.addProperty("cart", " ");
        }
        else
        {
            responseJsonObject.addProperty("cart", session.getAttribute("previousItems").toString());
        }
//        responseJsonObject.addProperty("movieList", "/cs122b-spring20/");
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }
}