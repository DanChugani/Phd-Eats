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
import android.widget.AdapterView;
import android.widget.ListView;
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
 * Name: Richard Clapham
 * Student #: 821-490-125
 *
 * Name: Chandan Chugani
 * Student #: RefactorThisToYourStudent#Chandan
 *
 * This is the select Restaurant screen if connected to the internet it will connect to a remote
 * database and load all of the available restaurants based off of the users choice of campus.
 * If you arent connected to the internet it will use a local database and load restaurants
 * associated with the sample local database.
 */
public class SelectRestaurant extends AppCompatActivity
{
    public final static String SER_KEY = "mycompany.myproject.ser";
    public final static String SER_KEY2 = "mycompany.myproject.ser2";
    public final static String SER_KEY3 = "mycompany.myproject.ser3";

    private FileIO myFileIO;
    private ArrayList<Restaurant> myArrayList;
    private ArrayList<Restaurant> myList;
    private ListView myListView;
    private Restaurant myRestaurant;

    //Oncreate method which will initiate onItemClickListeners as well as check network
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_restaurant);

        myFileIO = (FileIO)getIntent().getSerializableExtra(SER_KEY);
        myArrayList = (ArrayList<Restaurant>)getIntent().getSerializableExtra(SER_KEY2);
        myRestaurant = new Restaurant();

        myListView = (ListView) findViewById(R.id.listView2);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Checks to make sure the user still has an internet connection and handles accordingly
        //If the user had network connection but loses it part way through it will relaunch
        //the select campus screen and start it into offline mode
        if(isNetworkAvailable() && myFileIO.getMyNetworkState()) {
            new LoadRestaurantActivity(getBaseContext(), 0).execute(myFileIO.getMyCampusChoice());
        }
        else if (!isNetworkAvailable() && myFileIO.getMyNetworkState()){
            Toast.makeText(SelectRestaurant.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(SelectRestaurant.this, SelectCampus.class);
            Bundle myBundle = new Bundle();
            myBundle.putSerializable(SER_KEY, myFileIO);
            myBundle.putSerializable(SER_KEY2, myArrayList);
            myBundle.putSerializable(SER_KEY3, myRestaurant);
            myIntent.putExtras(myBundle);
            SelectRestaurant.this.startActivity(myIntent);
        }
        else if(!myFileIO.getMyNetworkState()){
            //Local DB
            localDB();
            //End of local DB
        }

        //Listeners for the user to select a restaurant it will then store the information of the
        //Restaurant and launch the selectedRestaurant class which will display Restaurant
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                myFileIO.setMyRestaurantChoice(myList.get(position).getRestaurantName());
                myRestaurant.setRestaurantName(myList.get(position).getRestaurantName());
                myRestaurant.setRestaurantLocation(myList.get(position).getRestaurantLocation());
                myRestaurant.setRestaurantDescription(myList.get(position).getRestaurantDescription());

                Intent myIntent = new Intent(SelectRestaurant.this, ShowRestaurantInfo.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable(SER_KEY, myFileIO);
                myBundle.putSerializable(SER_KEY2, myArrayList);
                myBundle.putSerializable(SER_KEY3, myRestaurant);
                myIntent.putExtras(myBundle);
                SelectRestaurant.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.home)
            return true;

        return super.onOptionsItemSelected(item);
    }

    //Checks if the device is a tablet returns true if it is
    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*Check if network is available
    * @return boolean if network is avaialble returns true
    */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //The local database method which will search the local databse for the users selected choice
    public void localDB()
    {
        myList = new ArrayList<>();
        for(int count=0; count < myArrayList.size(); count++) {
            if(myFileIO.getMyCampusChoice().equals(myArrayList.get(count).getCampus()))
                myList.add(myArrayList.get(count));
        }
        RestaurantAdapter arrayAdapter = new RestaurantAdapter(this, R.layout.restaurant_view, myList);
        myListView.setAdapter(arrayAdapter);
    }

    //This is the AsyncTask that will connect to the database and return a JSONArray of
    // Restaurants. It currently will dispaly the restaurants name location and description
    //which is all stored on the remote database.
    private class LoadRestaurantActivity extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        JSONArray stuff = null;
        private static final String TAG_JSONNAME = "stuff";
        private static final String TAG_RNAME = "RestaurantName";
        private static final String TAG_RLOCATION = "RestaurantLocation";
        private static final String TAG_RDESCRIPTION = "RestaurantDescription";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public LoadRestaurantActivity(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Creates a progressdialog so the user knows the app is performing actions.
        protected void onPreExecute()
        {
            myList = new ArrayList<>();
            builder = new ProgressDialog(SelectRestaurant.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.loading));
            builder.setMessage(getResources().getString(R.string.wait3));
            builder.show();
        }

        //Connects to database and recieves a JSONArray
        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method
                try{
                    String CampusName = (String)arg0[0];
                    String link = "http://phdeats.esy.es/getRestaurants3.php?CampusName="+CampusName;
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
                for (int i = 0; i < stuff.length(); i++) {

                    Restaurant myTemp = new Restaurant();

                    JSONObject c = stuff.getJSONObject(i);

                    String rName = c.getString(TAG_RNAME);
                    String rLocation = c.getString(TAG_RLOCATION);
                    String rDescription = c.getString(TAG_RDESCRIPTION);

                    myTemp.setRestaurantName(rName);
                    myTemp.setRestaurantLocation(rLocation);
                    myTemp.setRestaurantDescription(rDescription);

                    myList.add(myTemp);
                }
                myListView.setEmptyView(findViewById(R.id.empty_list_item));
                RestaurantAdapter arrayAdapter = new RestaurantAdapter(SelectRestaurant.this, R.layout.restaurant_view, myList);
                myListView.setAdapter(arrayAdapter);
            }
            catch (JSONException e) {e.printStackTrace();}
        }
    }
}

