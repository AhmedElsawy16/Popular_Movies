package com.example.android.sunshine.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView;
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
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_detail_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public static class DetailFragment extends Fragment {

        Database myDatabase;

        protected Movies mMovie;

        private ImageView mImageView;
        private TextView mTitleView;
        private TextView mOverviewView;
        private TextView mVoteAverageView;
        private TextView mReleaseDate;
        private TextView reviewsContent;

        private ImageButton Close;
        private Button Favorites;

        //private List<Videos> myVideos = new ArrayList<>();
        private TrailersListAdapter trailersListAdapter;
        private ReviewsListAdapter reviewsListAdapter;



        public DetailFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // mMovie contains the data of an one Movie get it from myMovies static variable
            mMovie = ForecastFragment.myMovies;
            myDatabase = new Database(getActivity());

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            //firstCard = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

            mImageView = (ImageView) rootView.findViewById(R.id.detail_image);
            mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
            mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
            mReleaseDate = (TextView) rootView.findViewById(R.id.detail_date);
            mVoteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);

            ListView VideosList = (ListView) rootView.findViewById(R.id.listview_trailers);
            ListView reviewsList = (ListView) rootView.findViewById(R.id.reviews_listView);
            reviewsContent = (TextView) rootView.findViewById(R.id.content_textview);
            Close = (ImageButton) rootView.findViewById(R.id.close_btn);
            // this is the MAKE AS FAVORITES button.
            Favorites = (Button) rootView.findViewById(R.id.add_to_favorites_btn);

            if(getArguments()!= null || getActivity().getIntent().getExtras() !=null){

                //call the function which will fetch the Trailers and Reviews of a Movie
                update();
                //check if the movie is in Database or not (Added to Favorites) to change the text
                if (myDatabase.getMoviesIds().contains(mMovie.get_id())){
                    Favorites.setText("DELETE FROM FAVORITES");
                }

                String image_url = "http://image.tmdb.org/t/p/w342" + mMovie.get_background_path();
                Picasso.with(getActivity()).load(image_url).into(mImageView);

                mTitleView.setText(mMovie.get_tittel());
                mOverviewView.setText(mMovie.get_overview());
                mReleaseDate.setText(mMovie.get_release_date());
                mVoteAverageView.setText(mMovie.get_vote_average());

                trailersListAdapter = new TrailersListAdapter(getActivity());
                VideosList.setAdapter(trailersListAdapter);
                // to solve the listView into ScrollView problem
                VideosList.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                // when click on the Videos listView
                VideosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Videos videos = trailersListAdapter.getItem(position);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videos.get_key())));
                    }
                });


                reviewsListAdapter = new ReviewsListAdapter(getActivity());
                reviewsList.setAdapter(reviewsListAdapter);
                // to solve the listView into ScrollView problem
                reviewsList.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                // when click on the reviews listView
                reviewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Reviews reviews = reviewsListAdapter.getItem(position);
                        reviewsContent.setText(reviews.get_content());
                        Close.setVisibility(view.VISIBLE);
                    }
                });

                // when click on Close button that in Detail Activity to remove the reviews content
                Close.setOnClickListener(new View.OnClickListener()   {
                    public void onClick(View v) {
                        reviewsContent.setText("");
                        Close.setVisibility(View.INVISIBLE);
                    }
                });

                // this function done when click on ADD TO FAVORITES button
                addData();
            }

// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                String date = DateUtils.formatDateTime(getActivity(),
//                        formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
//                mDateView.setText(date);
//                } catch (ParseException e) {
//                e.printStackTrace();
//                }

            // هنا علشان اجيب ال rating
            //mVoteAverageView.setText(Integer.toString(mMovie.get_rating));

//            Intent intent = getActivity().getIntent();
//            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
//                String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
//                ((TextView) rootView.findViewById(R.id.detail_text)).setText(forecastStr);
//            }
            return rootView;
        }

        // Done When Click on the MAKE AS FAVORITES button .
        public void addData(){
            Favorites.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            //if the arrayList that has all Favorites Movies ids contains this id
                                if (myDatabase.getMoviesIds().contains(mMovie.get_id())) {
                                    // if TRUE ==> Delete the Movie then change the text of button.
                                    int deleted = myDatabase.deletMovie(Integer.toString(mMovie.get_id()));
                                    Favorites.setText("MAKE AS FAVORITE");
                                    // if the function Delete has Done Display
                                    if (deleted > 0)
                                        Toast.makeText(getActivity(), "Movie Removed From Favorites", Toast.LENGTH_LONG).show();
                                    else
                                    // if NOT Done
                                        Toast.makeText(getActivity(), "Cannot Delete!", Toast.LENGTH_LONG).show();
                                }
                                // if the arrayList has NOT this Movie ID
                                else {
                                    // if TRUE ==> insert this movie into Database
                                    boolean inserted = myDatabase.insertData(mMovie);
                                    if (inserted == true) {
                                        Toast.makeText(getActivity(), "Movie added Successfully", Toast.LENGTH_LONG).show();
                                        Favorites.setText("DELETE FROM FAVORITES");
                                    } else
                                        Toast.makeText(getActivity(), "Data NOT Inserted", Toast.LENGTH_LONG).show();
                                }
                        }
                    }
            );
        }


        // function to show any message you want .
        public void showMessage(String title, String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }

        //this function to Always execute the Videos and Reviews Task .
        public void update(){
            FetchVideosTask VideosTask = new FetchVideosTask();
            FetchReviewsTask ReviewsTask = new FetchReviewsTask();
            // hena ana bgib el ID bta3 el film w ab3toh ll videosTask 3lshan ygib el video bta3 el film
            VideosTask.execute(Integer.toString(mMovie.get_id()));
            ReviewsTask.execute(Integer.toString(mMovie.get_id()));
        }

        @Override
        public void onStart(){
            super.onStart();
            //update();
        }

        // Class to fetch the Videos of an movie that extends from AsyncTask
        // the first param. in AsyncTask da eli hytb3tloh an the second param. da eli hyrg3 mnoh.
        public class FetchVideosTask extends AsyncTask<String, Void, List<Videos>>{

            private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

            @Override
            protected List<Videos> doInBackground(String... params){

                if (params.length == 0) {
                    return null;
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String VideosJsonStr = null;

                try {
                    final String Videos_Base_URL = "http://api.themoviedb.org/3/movie";
                    final String API_KEY = "api_key";

                    Uri uri = Uri.parse(Videos_Base_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath("videos")
                            .appendQueryParameter(API_KEY,BuildConfig.MOVIE_API_KEY)
                            .build();

                    URL url = new URL(uri.toString());

                    //to show the URI into Logcat
                    Log.v(LOG_TAG, "Built URI " + uri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null){

                        stringBuffer.append(line + "\n");
                    }

                    if(stringBuffer.length() == 0){
                        return null;
                    }

                    VideosJsonStr = stringBuffer.toString();

                    Log.v(LOG_TAG, "Videos string: " + VideosJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the Videos data, there's no point in attemping
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
                    return getVideosFromJson(VideosJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            private List<Videos> getVideosFromJson(String videosJsonStr) throws JSONException {

                final String RESULT = "results";
                final String ID = "id";
                final String KEY = "key";
                final String NAME = "name";

                JSONObject VideosJSON = new JSONObject(videosJsonStr);
                JSONArray arrOfTrailers = VideosJSON.getJSONArray(RESULT);

                List<Videos> lastResults = new ArrayList<>();

                for (int i = 0; i<arrOfTrailers.length(); i++) {

                    JSONObject oneVideo = arrOfTrailers.getJSONObject(i);
                    Videos video = new Videos(
                            oneVideo.getString(ID),
                            oneVideo.getString(KEY),
                            oneVideo.getString(NAME)
                    );

                    lastResults.add(video);
                }

                return lastResults;
            }

            @Override
            protected void onPostExecute(List<Videos> result){
                if (result != null) {
                    trailersListAdapter.clear();
                    for (Videos oneVideoStr : result) {
                        trailersListAdapter.add(oneVideoStr);
                    }
                }
            }

        }

        // Class to fetch the Reviews of an movie that extends from AsyncTask
        public class FetchReviewsTask extends AsyncTask<String, Void, List<Reviews>>{

            private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

            @Override
            protected List<Reviews> doInBackground(String... params){
                if (params.length == 0) {
                    return null;
                }

                HttpURLConnection urlConnection = null;
                BufferedReader bufferedReader = null;

                String reviewsJsonStr = null;

                final String Reviews_Base_Url = "http://api.themoviedb.org/3/movie";
                final String API_Key = "api_key";
                try {
                    Uri uri = Uri.parse(Reviews_Base_Url).buildUpon()
                            .appendPath(params[0])
                            .appendPath("reviews")
                            .appendQueryParameter(API_Key, BuildConfig.MOVIE_API_KEY)
                            .build();

                    URL url = new URL(uri.toString());
                    Log.v(LOG_TAG, "Built URI " + uri.toString());

                    urlConnection  = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();

                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        stringBuffer.append(line + "\n");
                    }

                    if(stringBuffer.length() == 0){
                        return null;
                    }

                    reviewsJsonStr = stringBuffer.toString();
                }
                catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getReviewsFromJson(reviewsJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            private List<Reviews> getReviewsFromJson(String ReviewsJsonStr)throws JSONException{
                final String RESULT = "results";
                final String ID = "id";
                final String AUTHOR = "author";
                final String CONTENT = "content";
                final String URL = "url";

                JSONObject ReviewsJson = new JSONObject(ReviewsJsonStr);
                JSONArray arrOfReviews = ReviewsJson.getJSONArray(RESULT);

                List<Reviews> ReviewsResult = new ArrayList<>();

                for (int i = 0; i<arrOfReviews.length(); i++){
                    JSONObject oneReview = arrOfReviews.getJSONObject(i);
                    Reviews reviews = new Reviews(
                            oneReview.getString(ID),
                            oneReview.getString(AUTHOR),
                            oneReview.getString(CONTENT),
                            oneReview.getString(URL)
                    );

                    ReviewsResult.add(reviews);
                }

                return ReviewsResult;
            }

            @Override
            protected void onPostExecute(List<Reviews> result){
                if (result != null) {
                    reviewsListAdapter.clear();
                    for (Reviews oneAuthorStr : result) {
                        reviewsListAdapter.add(oneAuthorStr);
                    }
                }
            }
        }
    }
}
