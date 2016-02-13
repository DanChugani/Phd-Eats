package mycompany.myproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Date: 10/31/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Chandan Chugani
 *
 * Description:
 * This is the Restaurant Information screen if connected to the internet it will connect to a remote
 * database and load all of the available Reviews based off of the users choice of campus and
 *  Restaurant. if you aren't connected to the internet it will use a local database and
 *  load restaurants associated with the sample local database.
 */
public class ShowRestaurantInfo extends AppCompatActivity
{
    public final static String SER_KEY = "mycompany.myproject.ser";
    public final static String SER_KEY2 = "mycompany.myproject.ser2";
    public final static String SER_KEY3 = "mycompany.myproject.ser3";

    private FileIO myFileIO;
    private ArrayList<Restaurant> myArrayList;
    private Restaurant myRestaurant;

    private ListView myListView;
    private ArrayList<Review> myList;
    private TextView restaurantNameTextView;
    private TextView restaurantLocationTextView;
    private TextView restaurantDescriptionTextView;
    private RatingBar rBRestaurantRating;

    // Oncreate will set up the appropriate information in the appropriate textfield
    // It will then either load reviews from remote database in online more or load from
    // local database in offline mode
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_restaurant);

        myFileIO = (FileIO)getIntent().getSerializableExtra(SER_KEY);
        myArrayList = (ArrayList<Restaurant>)getIntent().getSerializableExtra(SER_KEY2);
        myRestaurant = (Restaurant)getIntent().getSerializableExtra(SER_KEY3);

        restaurantNameTextView = (TextView)findViewById(R.id.textView4);
        restaurantLocationTextView = (TextView)findViewById(R.id.textView5);
        restaurantDescriptionTextView = (TextView)findViewById(R.id.textView6);
        rBRestaurantRating = (RatingBar) findViewById(R.id.ratingBar);
        myListView = (ListView) findViewById(R.id.listView3);

        restaurantNameTextView.setText(myRestaurant.getRestaurantName());
        restaurantLocationTextView.setText(myRestaurant.getRestaurantLocation());
        restaurantDescriptionTextView.setText(myRestaurant.getRestaurantDescription());

        // Checks to make sure the user still has an internet connection and handles accordingly
        // If the user had network connection but loses it part way through it will relaunch
        // The select campus screen and start it into offline mode
        if(isNetworkAvailable() && myFileIO.getMyNetworkState()) {
            new LoadReviewActivity(getBaseContext(), 0).execute(myFileIO.getMyCampusChoice(), myFileIO.getMyRestaurantChoice());
        }
        else if (!isNetworkAvailable() && myFileIO.getMyNetworkState()){
            Toast.makeText(ShowRestaurantInfo.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(ShowRestaurantInfo.this, SelectCampus.class);
            Bundle myBundle = new Bundle();
            myBundle.putSerializable(SER_KEY, myFileIO);
            myBundle.putSerializable(SER_KEY2, myArrayList);
            myBundle.putSerializable(SER_KEY3, myRestaurant);
            myIntent.putExtras(myBundle);
            ShowRestaurantInfo.this.startActivity(myIntent);
        }
        else if(!myFileIO.getMyNetworkState()){
            //Start fo localDB
            localDB();
            //end of local DB
        }

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // Checks for a drawable associated with Restaurant
        checkForDrawable();

        // Launches the submitReview activity if clicked
        Button button= (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(ShowRestaurantInfo.this, SubmitReview.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable(SER_KEY, myFileIO);
                myBundle.putSerializable(SER_KEY2, myArrayList);
                myBundle.putSerializable(SER_KEY3, myRestaurant);
                myIntent.putExtras(myBundle);
                ShowRestaurantInfo.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.action_settings) {
            return true;
        } */

        if (id == R.id.home)
            return true;

        return super.onOptionsItemSelected(item);
    }

    /*Check if network is available
    * @return boolean if network is available returns true
    */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //A void method which will take the restaurants Name and remove all commas and spaces
    //and move the string to lowercase and then check for a drawable with the same name
    public void checkForDrawable()
    {
        String myDrawableName = "na";
        try{
            myDrawableName = myRestaurant.getRestaurantName();
            myDrawableName = myDrawableName.replaceAll(" ", "");
            myDrawableName = myDrawableName.replace(".", "");
            myDrawableName = myDrawableName.toLowerCase();
            ImageView IV = (ImageView)findViewById(R.id.imageView);
            IV.setImageResource(getResources().getIdentifier(myDrawableName , "drawable", getPackageName()));
        }
        catch(Exception e){}
    }

    //Checks if the device is a tablet returns true if it is
    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //The local database method which will search the local database for reviews
    //associated with the campus name and restaurant name
    public void localDB()
    {
        myRestaurant = new Restaurant();

        for(int count=0; count < myArrayList.size(); count++) {
            if(myFileIO.getMyCampusChoice().equals(myArrayList.get(count).getCampus()) && myFileIO.getMyRestaurantChoice().equals(myArrayList.get(count).getRestaurantName()))
                myRestaurant = myArrayList.get(count);
        }

        myRestaurant.calculateRating();
        rBRestaurantRating.setRating(myRestaurant.getRestaurantRating());

        myList = new ArrayList<>();
        for(int count=0; count < myRestaurant.getAmountofReviews(); count++) {
            myList.add(myRestaurant.getReview(count));
        }

        ReviewAdapter arrayAdapter = new ReviewAdapter(this, R.layout.review_view, myList);
        myListView.setAdapter(arrayAdapter);
    }

    //This is the AsyncTask that will connect to the database and return a JSONArray of
    // Reviews. It will recieve all reviews associated with that specific restaurant and will
    //calaculate the average rating of the restaurant based on the reviews returned
    private class LoadReviewActivity extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        JSONArray stuff = null;
        private static final String TAG_JSONNAME = "stuff";
        private static final String TAG_UNAME = "Username";
        private static final String TAG_RATING = "Rating";
        private static final String TAG_DESCRIPTION = "Description";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public LoadReviewActivity(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Creates a progressdialog so the user knows the app is performing actions.
        protected void onPreExecute()
        {
            myList = new ArrayList<>();
            builder = new ProgressDialog(ShowRestaurantInfo.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.loading));
            builder.setMessage(getResources().getString(R.string.wait4));
            builder.show();
        }

        //Connects to database and recieves a JSONArray
        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method
                try{
                    String CampusName = (String)arg0[0];
                    String RestaurantName = (String)arg0[1];
                    String link = "link to get reviews from campus"+CampusName+"& RestaurantName="+RestaurantName;
                    link = link.replaceAll(" ", "%20");

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    HttpEntity httpEntity = response.getEntity();
                    String myResponse = EntityUtils.toString(httpEntity);

                    // Making a request to url and getting response
                    Log.d("Response: ", "> " + myResponse);

                    if (myResponse != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(myResponse);
                            // Getting JSON Array node
                            stuff = jsonObj.getJSONArray(TAG_JSONNAME);
                        }
                        catch (JSONException e) {e.printStackTrace();}
                    }
                    else {Log.e("ServiceHandler", "Couldn't get any data from the url");}
                    return null;
                }
                catch(Exception e){return "Exception: " + e.getMessage();}
            }
            else{return "False";}
        }

        //Iterates through the data gathered from the return string and displays it accordingly.
        @Override
        protected void onPostExecute(String result){
            builder.dismiss();
            try {
                // looping through All Contacts
                for (int i = 0; i < stuff.length(); i++)
                {
                    Review myTemp = new Review();

                    JSONObject c = stuff.getJSONObject(i);

                    String username = c.getString(TAG_UNAME);
                    float rating = Float.parseFloat(c.getString(TAG_RATING));
                    String description = c.getString(TAG_DESCRIPTION);

                    myTemp.setUserName(username);
                    myTemp.setRating(rating);
                    myTemp.setDescription(description);

                    myRestaurant.addReview(myTemp);
                    myList.add(myTemp);
                }
                myRestaurant.calculateRating();
                rBRestaurantRating.setRating(myRestaurant.getRestaurantRating());
                myListView.setEmptyView(findViewById(R.id.empty_list_item));
                ReviewAdapter arrayAdapter = new ReviewAdapter(ShowRestaurantInfo.this, R.layout.review_view, myList);
                myListView.setAdapter(arrayAdapter);
            }
            catch (JSONException e) {e.printStackTrace();}
        }
    }
}
