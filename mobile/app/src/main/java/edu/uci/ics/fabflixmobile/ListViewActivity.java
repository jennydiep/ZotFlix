package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends Activity {
    private String url;
    private EditText searchBar;
    private Button searchButton;
    private Button prevButton;
    private Button nextButton;
    private ArrayList<Movie> movies;
    private MovieListViewAdapter adapter;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        movies = new ArrayList<>();
        page = 0;

        adapter = new MovieListViewAdapter(movies, this);

        searchBar = findViewById(R.id.searchText);
        searchButton = findViewById(R.id.searchButton);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        url = "http://ec2-3-18-107-131.us-east-2.compute.amazonaws.com:8080/cs122b-spring20/api/";

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent singleMoviePage = new Intent(ListViewActivity.this, SingleMovieViewActivity.class);

                // pass id to single movie page
                System.out.println(movie.getId());
                singleMoviePage.putExtra("id", movie.getId());
                //without starting the activity/page, nothing would happen
                startActivity(singleMoviePage);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // go to next page if it exists
                // TODO bug to fix later if there are exactly 20 entries
                if (movies.size() == 20)
                {
                    page++;
                    searchResult(page);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {
                // go to previous page unless on first page do nothing
                if (page <= 0)
                {
                    page = 0;
                }
                else
                {
                    page--;
                    searchResult(page);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // initiate new search,  represents first page
                searchResult(0);
            }
        });
    }
    public void searchResult(int page) {

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // TODO encode title for url
        String title = searchBar.getText().toString();
        String records = "&records=20";
        String offset = "&offset=" + page*20;
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "search?title=" + title + records + offset, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response: " + response);
                try {
                    //clear previous search
                    movies.clear();
                    // convert response to jsonarray
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 1; i < jsonArray.length(); i++)
                    {
                        // gets each json object which is information for one movie
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        String id = jsonObject.get("id").toString();
                        String title = jsonObject.get("title").toString();
                        String temp = jsonObject.get("year").toString();
                        int year = Integer.parseInt(temp);
                        String director = jsonObject.get("director").toString();

                        // splits stars ex: "nm1305990 Kristina Nikolova Dalio,nm1533878 Mike Meiners"
                        String[] starsArr = (jsonObject.get("stars").toString()).split(",");

//                        String[] genresArr = (jsonObject.get("genres").toString()).split(",");
                        String genres = jsonObject.get("genres").toString();

                        movies.add(new Movie(id, title, year, director, starsArr, genres));

                        // update adapter since movies array list changed
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("result error: " + e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("search.error", error.toString());
                    }
                }) {
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }
}