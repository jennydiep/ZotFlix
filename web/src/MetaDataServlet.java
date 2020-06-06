import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Buffer;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


// Declaring a WebServlet called MetaDataServlet, which maps to url "/api/stars"
@WebServlet(name = "MetaDataServlet", urlPatterns = "/api/metadata")
public class MetaDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Get metadata from connection
            DatabaseMetaData metaData = dbcon.getMetaData();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            ArrayList<String> tables = new ArrayList();
            String table[] = {"TABLE"};
            ResultSet rs = metaData.getTables(null, null, null, table);



            // Iterate through each row of rs
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
//                String tableName = rs.getString("TABLE_NAME");
//
//                // Create a JsonObject based on the data we retrieve from rs
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("tableName", tableName);
//
//                jsonArray.add(jsonObject);
            }

            JsonArray jsonArray = new JsonArray();

            for (String t : tables)
            {
                ResultSet rsCol = metaData.getColumns(null, null, t, null);
                JsonArray jsonArrayCol = new JsonArray();
                while (rsCol.next())
                {
                    String colName = rsCol.getString("COLUMN_NAME");
                    String colType = rsCol.getString("TYPE_NAME");
                    String colSize = rsCol.getString("COLUMN_SIZE");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("tableName", t);
                    jsonObject.addProperty("colName", colName);
                    jsonObject.addProperty("colType", colType);
                    jsonObject.addProperty("colSize", colSize);
                    jsonArrayCol.add(jsonObject);
                }
                jsonArray.add(jsonArrayCol);

            }


            // write JSON string to output
            out.write(jsonArray.toString());
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
