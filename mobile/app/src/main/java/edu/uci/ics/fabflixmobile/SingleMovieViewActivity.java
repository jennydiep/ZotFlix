package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieViewActivity extends Activity {
    private String url;
    private Movie movie;

    TextView titleView;
    TextView yearView;
    TextView directorView;
    TextView starsView;
    TextView genresView;
//    private SingleMovieViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("started single movie page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);
        url = "http://ec2-3-18-107-131.us-east-2.compute.amazonaws.com:8080/cs122b-spring20/api/";
        String[] tempStars = new String[]{};
        movie = new Movie("id", "title",0, "director", tempStars, "genres");
//        ListView listView = findViewById(R.id.relative);
//        ListView view = findViewById(R.id.movieView);
//        adapter = new SingleMovieViewAdapter(movie, this);
//        view.setAdapter(adapter);

        // get id from movie list
        Intent intent = getIntent(); // intent from ListViewActivity
        String id = intent.getStringExtra("id");

        titleView = findViewById(R.id.title);
        yearView = findViewById(R.id.year);
        directorView = findViewById(R.id.director);
        starsView = findViewById(R.id.stars);
        genresView = findViewById(R.id.genres);

        System.out.println("setting single movie page information");
        // talks to backend to find single movie information from movie id
        moviePage(id);

    }

    // talks to backend to receive information of movie from id given
    public void moviePage(String id) {

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest singleMovieRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("singleMovie.success", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // gets each json object which is information for one movie

                    // json formatted where position 1 = stars information
                    // position 2 = genres
                    // position 3 = rating

                    // contains information each jsonObject is a star of that movie
                    JSONArray jsonArrayMovie = (JSONArray) jsonArray.get(0);
                    JSONObject jsonObject = (JSONObject) jsonArrayMovie.get(0);
                    String title = jsonObject.get("title").toString();
                    int year = Integer.parseInt(jsonObject.get("year").toString());
                    String director = jsonObject.get("director").toString();

//                    // formatting stars into array of strings
//                    String[] starsArr = (jsonObject.get("stars").toString().split(","));
//                    for (int i = 0; i < starsArr.length; i++)
//                    {
//                        // getting the star name only
//                        starsArr[i] = starsArr[i].substring(starsArr[i].indexOf(' ')+1);
//                    }
                    JSONArray jsonArrayStars = (JSONArray) jsonArray.get(0);
                    String[] starsArr = new String[jsonArrayStars.length()];
                    for (int i = 0; i < jsonArrayStars.length(); i++)
                    {
                        JSONObject jsonObjectStar = (JSONObject) jsonArrayStars.get(i);
                        starsArr[i]  = jsonObjectStar.get("star_name").toString();

                    }




                    // listing all genres for single movie page
                    JSONArray jsonArrayGenres = (JSONArray) jsonArray.get(1);
                    ArrayList<String> genresArr = new ArrayList<>();

                    for (int i = 0; i < jsonArrayGenres.length(); i++)
                    {
                        JSONObject jsonObjectGenre = (JSONObject) jsonArrayGenres.get(i);
                        genresArr.add(jsonObjectGenre.get("genre_name").toString());
                    }

                    System.out.println(title);
                    // save movie information for front end
                    movie = new Movie(id, title, year, director, starsArr, genresArr.toString());

                    // setting views
                    titleView.setText(movie.getName());
                    yearView.setText(movie.getYear() + "");
                    directorView.setText(movie.getDirector());

                    String tempstars = movie.getStars()[0];

                    for (int i = 1; i < movie.getStars().length; i++)
                    {
                        tempstars += ", " + movie.getStars()[i];
                    }

                    starsView.setText(tempstars);
                    genresView.setText(movie.getGenres());
//                    adapter.notifyDataSetChanged();
                    System.out.println(movie.getName());

                    Log.d("singleMovie.success", response);

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("error: " + e.toString());
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("singleMovie.error", error.toString());
                    }
                }) {
        };
        // !important: queue.add is where the login request is actually sent
        queue.add(singleMovieRequest);
    }

}
