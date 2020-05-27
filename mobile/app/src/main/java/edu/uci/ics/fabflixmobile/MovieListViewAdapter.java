package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView yearView = view.findViewById(R.id.year);
        TextView directorView = view.findViewById(R.id.director);
        TextView starsView = view.findViewById(R.id.stars);
        TextView genresView = view.findViewById(R.id.genres);

        String[] starsArr = movie.getStars();

        // only display 3 stars
        String stars = starsArr[0].substring(starsArr[0].indexOf(' ')+1);
        for (int j = 1; j < Math.min(3, starsArr.length); j++)
        {
            stars += ", " + starsArr[j].substring(starsArr[j].indexOf(' ')+1); // star name
        }

        titleView.setText(movie.getName());
        yearView.setText(movie.getYear() + "");// need to cast the year to a string to set the label
        directorView.setText(movie.getDirector());
        starsView.setText(stars);
        genresView.setText(movie.getGenres());

        return view;
    }
}