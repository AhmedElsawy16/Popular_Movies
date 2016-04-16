package com.example.android.sunshine.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ForecastFragment extends Fragment {
    // da variable h7ot feh el data bta3ty eli rag3a mn el JSON
    public static Movies myMovies;
    //static variable to check if the View is Tablet or NOT
    public static boolean towPlan = false;

    Database myDatabase;

    private static final String SORT_SETTING = "sort_setting";
    private static final String POPULARITY_DESC = "popularity.desc";
//    private static final String RATING_DESC = "vote_average.desc";
    private String mSortBy = POPULARITY_DESC;

    private Adapter MovieAdapter;
    public ForecastFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
//        inflater.inflate(R.menu.forecastfragment, menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){        // de function btrn lma ados 3la zorar f el menu
//        int id = item.getItemId();
//        if (id == R.id.action_refresh){             // lw el zorar eli ana dost 3lih el ID bta3o action_refresh
//            ubdateMovies("sort_by");
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        MovieAdapter = new Adapter(getActivity());

        gridView.setAdapter(MovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movies movie = MovieAdapter.getItem(position);
                myMovies = movie;
                // this bundle just to listen if i clicked on an item in grid view or not
                // and send a string to the activity to not be null (NULL POINTER EXCEPTION).
                Bundle bundle=new Bundle();
                bundle.putString("AA",movie.toString());

                if (towPlan){
                    // if the view is a Tablet
                    // replace the right view with detail fragment
                    DetailActivity.DetailFragment gg = new DetailActivity.DetailFragment();
                    gg.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.movies_detail_container,gg )
                            .commit();
                }else {
                    // if view is a phone
                    // just intent to the activity
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }
            }
        });
        myDatabase = new Database(getActivity());

        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_SETTING)) {
            mSortBy = savedInstanceState.getString(SORT_SETTING);
        }

        return rootView;
//        ListView listView = (ListView) rootView.findViewById(R.id.forecast_listview);
//        listView.setAdapter(arrayAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
//                    String forecast = arrayAdapter.getItem(position);
//                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
//                    startActivity(intent);
//                }
//        });
    }

//    public void viewAll(){
//
//        Show.setOnClickListener(
//                new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v) {
//                        Cursor res = myDatabase.selectAll();
//                        if (res.getCount() == 0){
//                            // show Message
//                            showMessage("Error", "Nothing Found :(");
//                            return;
//                        }
//
//                        StringBuffer buffer = new StringBuffer();
//                        while (res.moveToNext()){
//                            buffer.append("ID : " + res.getString(0) + "\n");
//                            buffer.append("POSTER : " + res.getString(1) + "\n");
//                        }
//                        // Show Message
//                        showMessage("Data", buffer.toString());
//                    }
//                }
//        );
//    }




    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    // function to fetch the Movies from the Server
    public void updateMovies()
    {
        FetchMoviesTask MovieTask = new FetchMoviesTask();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String SORT_BY = preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_most_popular));

        String PAGE_NUM = preferences.getString(getString(R.string.pref_num_of_page_key),
                getString(R.string.pref_default_num));

        if (SORT_BY .equalsIgnoreCase("Favorites")) {
            viewAll();
        }
        else {
            MovieTask.execute(SORT_BY, PAGE_NUM);
        }
    }

    // function to passing the data into Database to the Adapter (MovieAdapter).
    public void viewAll(){
        List<Movies> favorites ;

        favorites = myDatabase.selectAll();
        if (favorites.size()== 0){
            // show Message
            showMessage("Error", "There Is NO Movies IN Your Favorites");
            return;
        }


        if (favorites != null) {
            MovieAdapter.clear();
            for (Movies oneMovieStr : favorites) {
                MovieAdapter.add(oneMovieStr);
            }
        }
        else
            showMessage("Error", "Nothing Found!");
    }

    @Override
    public void onStart(){          // de function bt7sal f el awl 5als ^_^
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSortBy != POPULARITY_DESC) {
            outState.putString(SORT_SETTING, mSortBy);
            }
        super.onSaveInstanceState(outState);
        }

    // class to Fetch the Movies Data Form the Server
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies> >{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        // de el function eli htrg3ly el data eli gya mn el JSON
        private List<Movies> getMoviesDataFromJson(String MoviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULT = "results";
            final String OWM_ID = "id";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_TITLE = "title";
            final String OWM_BACKGROUND_PATH = "backdrop_path";
            final String OWM_POPULARITY = "popularity";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject MovieJSON = new JSONObject(MoviesJsonStr);
            JSONArray moviesArray = MovieJSON.getJSONArray(OWM_RESULT);

            List<Movies> result = new ArrayList<>();

            for(int i = 0; i < moviesArray.length(); i++) {

                JSONObject movie = moviesArray.getJSONObject(i);
                Movies OneMovie = new Movies(
                        movie.getInt(OWM_ID),
                        movie.getString(OWM_POSTER_PATH),
                        movie.getString(OWM_OVERVIEW),
                        movie.getString(OWM_TITLE),
                        movie.getString(OWM_BACKGROUND_PATH),
                        movie.getString(OWM_POPULARITY),
                        movie.getString(OWM_VOTE_AVERAGE),
                        movie.getString((OWM_RELEASE_DATE))
                );

                result.add(OneMovie);
            }

            // show the data into Logcat
            for (Object s : result) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return result;

        }
        @Override
        protected List<Movies> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;



            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String PAGE = "page";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(PAGE, params[1])
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + MovieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movies> result) {
            if (result != null) {
                MovieAdapter.clear();
                for (Movies oneMovieStr : result) {
                    MovieAdapter.add(oneMovieStr);
                }
            }
        }
    }

}
