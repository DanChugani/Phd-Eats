package mycompany.myproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Created by Rick
 * This is the submit review screen a simple screen that will allow the user to submit a description
 * and rating of the restaurant
 */

public class SubmitReview extends AppCompatActivity
{
    public final static String SER_KEY = "mycompany.myproject.ser";
    public final static String SER_KEY2 = "mycompany.myproject.ser2";
    public final static String SER_KEY3 = "mycompany.myproject.ser3";

    private FileIO myFileIO;
    private ArrayList<Restaurant> myArrayList;
    private Restaurant myRestaurant;
    private Review myReview;
    private RatingBar ratingRatingBar;
    private EditText descriptionEditText;
    private Button submitButton;

    //The onCreate will assign the appropriate fields
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);

        myFileIO = (FileIO)getIntent().getSerializableExtra(SER_KEY);
        myArrayList = (ArrayList<Restaurant>)getIntent().getSerializableExtra(SER_KEY2);
        myRestaurant = (Restaurant)getIntent().getSerializableExtra(SER_KEY3);
        ratingRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        descriptionEditText = (EditText) findViewById(R.id.editText3);
        submitButton = (Button) findViewById(R.id.button3);
        myReview = new Review();

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //Checks if the user has a netowkr connection if they dont it will disable the submit a review
        //button and display a toast explaining why
        if(!isNetworkAvailable()){
            submitButton.setEnabled(false);
            Toast.makeText(SubmitReview.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
        }

        //The listener when clicked will check the entered fields to make sure they meet the criteria
        //which is currently a rating and review must be entered as well as the review cannot
        //exceed 25 characters in length. The button will also check for network connection again
        // before submitting the review
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionEditText.getText().toString().length() < 30) {
                    if ((descriptionEditText.getText().toString().length() > 0) && !(ratingRatingBar.getRating() == 0)) {
                        myReview.setUserName(readMyName());
                        myReview.setRating(ratingRatingBar.getRating());
                        myReview.setDescription(descriptionEditText.getText().toString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(SubmitReview.this);
                        builder.setCancelable(false);
                        builder.setTitle(getResources().getString(R.string.Confirm1));
                        builder.setMessage(getResources().getString(R.string.check));
                        builder.setPositiveButton(getResources().getString(R.string.Confirm2), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //connect to DB add review here
                                if (isNetworkAvailable() && myFileIO.getMyNetworkState()) {
                                    new SubmitReviewActivity(getBaseContext(), 0).execute(myReview.getUserName(), Float.toString(myReview.getRating()), myReview.getDescription());
                                } else if (!isNetworkAvailable() && myFileIO.getMyNetworkState()) {
                                    Toast.makeText(SubmitReview.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(SubmitReview.this, SelectCampus.class);
                                    Bundle myBundle = new Bundle();
                                    myBundle.putSerializable(SER_KEY, myFileIO);
                                    myBundle.putSerializable(SER_KEY2, myArrayList);
                                    myBundle.putSerializable(SER_KEY3, myRestaurant);
                                    myIntent.putExtras(myBundle);
                                    SubmitReview.this.startActivity(myIntent);
                                }
                                myRestaurant.addReview(myReview);
                                Intent myIntent = new Intent(SubmitReview.this, ShowRestaurantInfo.class);
                                Bundle myBundle = new Bundle();
                                myBundle.putSerializable(SER_KEY, myFileIO);
                                myBundle.putSerializable(SER_KEY2, myArrayList);
                                myBundle.putSerializable(SER_KEY3, myRestaurant);
                                myIntent.putExtras(myBundle);
                                SubmitReview.this.startActivity(myIntent);
                            }
                        });
                        builder.setNegativeButton(getResources().getString(R.string.Cancel1), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(SubmitReview.this, getResources().getString(R.string.Cancel2), Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(SubmitReview.this, ShowRestaurantInfo.class);
                                Bundle myBundle = new Bundle();
                                myBundle.putSerializable(SER_KEY, myFileIO);
                                myBundle.putSerializable(SER_KEY2, myArrayList);
                                myBundle.putSerializable(SER_KEY3, myRestaurant);
                                myIntent.putExtras(myBundle);
                                SubmitReview.this.startActivity(myIntent);
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(SubmitReview.this, getResources().getString(R.string.incorrect5), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SubmitReview.this, getResources().getString(R.string.incorrect7), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit_review, menu);
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

    //A string method that will read the users information from a local file. This file is meant
    //to contain the users personal settings however it currently only contains the suers name
    public String readMyName()
    {
        File file = new File(getFilesDir(),"myUser.txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = buffReader.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        }
        return text.toString();
    }

    //This is the AsyncTask that will be used assuming that the users review meets the criteria. If
    //the review meets the criteria this method will send the users review to the database
    private class SubmitReviewActivity  extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        private String myReturn = " ";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public SubmitReviewActivity(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Creates a progressdialog so the user knows the app is performing actions.
        protected void onPreExecute()
        {
            builder = new ProgressDialog(SubmitReview.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.reviewSubmitting));
            builder.setMessage(getResources().getString(R.string.wait5));
            builder.show();
        }

        //Submits the review to the database
        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method

                try{
                    String campusname = myFileIO.getMyCampusChoice();
                    String restaurantname = myFileIO.getMyRestaurantChoice();
                    String username = (String)arg0[0];
                    String rating = (String)arg0[1];
                    float myRating = Float.parseFloat(rating);
                    String description = (String)arg0[2];
                    String link = "link to submit review to resteraunt on campus"+campusname+"& restaurantname="+restaurantname+"& username="+username+"& rating="+myRating+"& description="+description;
                    link = link.replaceAll(" ", "%20");

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    setMyReturn(sb.toString());
                    return sb.toString();
                }
                catch(Exception e){Toast.makeText(context, getResources().getString(R.string.reviewF), Toast.LENGTH_SHORT).show(); return new String("Exception: " + e.getMessage());}
            }
            else {Toast.makeText(context, getResources().getString(R.string.reviewF), Toast.LENGTH_SHORT).show(); return "False";}
        }

        //Dispalys toast tot the user informing them that the review has been submitted.
        @Override
        protected void onPostExecute(String result){
            builder.dismiss();
            Toast.makeText(context, getMyReturn(), Toast.LENGTH_SHORT).show();
        }

        protected void setMyReturn(String i){myReturn = i;}
        protected String getMyReturn(){return myReturn;}
    }
}
