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
 * Date: 10/31/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Dan Chugani
 *
 * Description:
 * This is the select campus screen if connected to the internet it will connect to a remote
 * database and load all of the available campuses to choose from. If you arent connected
 * to the internet it will use a local database.
 */

public class SelectCampus extends AppCompatActivity {
    public final static String SER_KEY = "mycompany.myproject.ser";
    public final static String SER_KEY2 = "mycompany.myproject.ser2";

    private FileIO myFileIO;
    private ArrayList<Restaurant> myArrayList;
    private ListView myListView;
    private ArrayList<Campus> myList;

    //Oncreate method which will initiate onItemClickListeners as well as check network
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_campus);

        myFileIO = new FileIO();
        myArrayList = new ArrayList<>();
        myList = new ArrayList<>();

        myListView = (ListView) findViewById(R.id.listView);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // Checks for internet connection and if it is available connects to the reote database
        // Else it will use the local database
        if (isNetworkAvailable()) {
            myFileIO.setMyNetworkState(true);
            new LoadCampusActivity(getBaseContext(), 0).execute();
        } else {
            //Start of local DB
            Toast.makeText(SelectCampus.this,getResources().getString(R.string.offline), Toast.LENGTH_LONG).show();
            myFileIO.setMyNetworkState(false);
            localDB();
            //End of local DB
        }

        // Will listen for user choice on the listView will then store the users selectedCampus
        // As a string for later use and the launches the SelectRestaurant Screen
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                myFileIO.setMyCampusChoice(myList.get(position).getCampusName());

                Intent myIntent = new Intent(SelectCampus.this, SelectRestaurant.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable(SER_KEY, myFileIO);
                myBundle.putSerializable(SER_KEY2, myArrayList);
                myIntent.putExtras(myBundle);
                SelectCampus.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_campus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        } */

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

    //The local database method which will create a local database of restaurants
    public void localDB() {
        myArrayList.add(new Restaurant("Campus 1", "Location 1", "Description 1", "Restaurant 1", "Description 1", "Location 1"));
        myArrayList.add(new Restaurant("Campus 1", "Location 1", "Description 1", "Restaurant 2", "Description 2", "Location 2"));
        myArrayList.add(new Restaurant("Campus 2", "Location 2", "Description 2", "Restaurant 1", "Description 1", "Location 1"));
        myArrayList.add(new Restaurant("Campus 2", "Location 2", "Description 2", "Restaurant 2", "Description 2", "Location 2"));
        myArrayList.add(new Restaurant("Campus 2", "Location 2", "Description 2", "Restaurant 3", "Description 3", "Location 3"));
        myArrayList.add(new Restaurant("Campus 2", "Location 2", "Description 2", "Restaurant 4", "Description 4", "Location 4"));

        for (int count = 0; count < myArrayList.size(); count++) {
            myArrayList.get(count).addReview(new Review("User 1", 0, "Sample Description 1"));
            myArrayList.get(count).addReview(new Review("User 2", 5, "Sample Description 2"));
            myArrayList.get(count).addReview(new Review("User 3", 4, "Sample Description 3"));
            myArrayList.get(count).addReview(new Review("User 4", 2, "Sample Description 4"));
            myArrayList.get(count).addReview(new Review("User 5", 5, "Sample Description 5"));
            myArrayList.get(count).addReview(new Review("User 6", 5, "Sample Description 6"));
        }

        ArrayList<Campus> myTempList = new ArrayList<>();
        for (int count = 0; count < myArrayList.size(); count++) {
            boolean contains = false;
            if(!myTempList.isEmpty()) {
                for (int count2 = 0; count2 < myTempList.size(); count2++) {
                    if (myArrayList.get(count).getCampus().equals(myTempList.get(count2).getCampusName())) {
                        contains = true;
                    }
                }
                if(!contains){
                    myTempList.add(new Campus(myArrayList.get(count).getCampus(), myArrayList.get(count).getCLocation(), myArrayList.get(count).getCDescription()));
                }
            }
            else{
                myTempList.add(new Campus(myArrayList.get(count).getCampus(), myArrayList.get(count).getCLocation(), myArrayList.get(count).getCDescription()));
            }
        }
        myList.addAll(myTempList);

        CampusAdapter arrayAdapter = new CampusAdapter(SelectCampus.this, R.layout.campus_view, myList);
        myListView.setAdapter(arrayAdapter);
    }

    // This is the AsyncTask that will connect to the database and return a JSONArray of
    // Campuses. It currently will only display the campus however the other information is
    // Readily available if needed
    private class LoadCampusActivity extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost = 0;
        JSONArray stuff = null;
        private static final String TAG_JSONNAME = "stuff";
        private static final String TAG_CNAME = "CampusName";
        private static final String TAG_CLOCATION = "CampusLocation";
        private static final String TAG_CDESCRIPTION = "CampusDescription";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public LoadCampusActivity(Context context, int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Creates a progressdialog so the user knows the app is performing actions.
        protected void onPreExecute() {
            builder = new ProgressDialog(SelectCampus.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.loading));
            builder.setMessage(getResources().getString(R.string.wait2));
            builder.show();
        }

        //Connects to database and recieves a JSONArray
        @Override
        protected String doInBackground(String... arg0) {
            if (byGetOrPost == 0) { //means by Get Method
                try {
                    String link = "link to get campuses database";
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                    return null;
                } catch (Exception e) {
                    return "Exception: " + e.getMessage();
                }
            } else {
                return "False";
            }
        }

        //Iterates through the data gathered from the return string and displays it.
        @Override
        protected void onPostExecute(String result) {
            builder.dismiss();
            try {
                // looping through All Contacts
                for (int i = 0; i < stuff.length(); i++) {

                    Campus myTemp = new Campus();

                    JSONObject c = stuff.getJSONObject(i);

                    String cName = c.getString(TAG_CNAME);
                    String cLocation = c.getString(TAG_CLOCATION);
                    String cDescription = c.getString(TAG_CDESCRIPTION);

                    myTemp.setCampusName(cName);
                    myTemp.setCampusLocation(cLocation);
                    myTemp.setCampusDescription(cDescription);

                    myList.add(myTemp);
                }
                myListView.setEmptyView(findViewById(R.id.empty_list_item));
                CampusAdapter arrayAdapter = new CampusAdapter(SelectCampus.this, R.layout.campus_view, myList);
                myListView.setAdapter(arrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
