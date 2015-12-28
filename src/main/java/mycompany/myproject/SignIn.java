package mycompany.myproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

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
 * Created by Rick on 19/2015.
 * This is the main screen of the application it will allow th user to sign in as a guest or
 * using actual credentials assuming you aren't disconnected from the internet. There is a checkbox
 * that will allow the user to save there credentials
 */

public class SignIn extends AppCompatActivity
{
    public final static String SER_KEY = "mycompany.myproject.ser";
    public final static String SER_KEY2 = "mycompany.myproject.ser2";

    private FileIO myFileIO;

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private EditText UsernameEditText;
    private EditText PasswordEditText;
    private Button SignInButton;
    private Button GuestButton;
    private TextView signUpTextView;
    private String myName;
    private String myPassword;

    /*
     * The main oncreate method applys on click listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        myFileIO = new FileIO();

        UsernameEditText = (EditText) findViewById(R.id.editText);
        PasswordEditText = (EditText) findViewById(R.id.editText3);
        SignInButton = (Button) findViewById(R.id.button);
        GuestButton = (Button) findViewById(R.id.button4);
        signUpTextView = (TextView) findViewById(R.id.textView7);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.checkBox);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        //loads saved login information
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            UsernameEditText.setText(loginPreferences.getString("username", ""));
            PasswordEditText.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

        ///Checks to see if network is disabled if it is disables the main signin button
        if(!isNetworkAvailable()){
            SignInButton.setEnabled(false);
        }

        //When the button is clicked will check to see if username and password is entered
        //If username and password is entered checks for internet access again and if present
        // will login online mode or if not it will disblay network connection lost
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myName = UsernameEditText.getText().toString().toLowerCase();
                myPassword = PasswordEditText.getText().toString();
                if (myName.length() > 0 && myPassword.length() > 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(UsernameEditText.getWindowToken(), 0);

                    if(isNetworkAvailable()) {
                        new SignInActivity(getBaseContext(), 0).execute(myName, myPassword);
                    }
                    else {
                        Toast.makeText(SignIn.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
                        SignInButton.setEnabled(false);
                    }

                } else {
                    Toast.makeText(SignIn.this, getResources().getString(R.string.incorrect3), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Allows the user to sign in as a guest in either online or offline mode
        GuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMyName("Guest");
                Intent myIntent = new Intent(SignIn.this, SelectCampus.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable(SER_KEY, myFileIO);
                myIntent.putExtras(myBundle);
                SignIn.this.startActivity(myIntent);
            }
        });

        //launches the signup activity
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SignIn.this, SignUp.class);
                Bundle myBundle = new Bundle();
                myIntent.putExtras(myBundle);
                SignIn.this.startActivity(myIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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

        return super.onOptionsItemSelected(item);
    }

    /*Creates a file holding the users personal information which is currently only the username
     * @param String i recieves the username from the user and writes to file
     */
    public void createMyName(String i){
        StringBuilder strFileContents = new StringBuilder("");

        String content = i;
        strFileContents.append(content);
        content = strFileContents.toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("myUser.txt", MODE_PRIVATE);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    //Checks if the device is a tablet returns true if it is
    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*
     * Asynchronous task that will connect to the remote database and attempt tog the user into
     * the database
     */
    private class SignInActivity  extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        private String myCheck= "";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public SignInActivity(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Disables signinbutton and guestloginbutton temporarily so the user cannot flood
        // the database and enables a progressdialog so the user nkows the app is performing
        //actions
        protected void onPreExecute()
        {
            SignInButton.setEnabled(false);
            GuestButton.setEnabled(false);
            builder = new ProgressDialog(SignIn.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.signingIn));
            builder.setMessage(getResources().getString(R.string.wait1));
            builder.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method

                try{
                    String username = (String)arg0[0];
                    String password = (String)arg0[1];
                    String link = "http://phdeats.esy.es/getLogin.php?username="+username+"& password="+password;
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
                    myCheck = sb.toString();
                    return sb.toString();
                }

                catch(Exception e){
                    return "Exception: " + e.getMessage();
                }
            }
            else{return "False";}
        }

        //Checks to see if login was sucessful if it was logs the user into the app
        // if you entered the wrong credentials a message will be displayed statign invalid username
        // or password if your login was a sucess your credntials will be saved
        @Override
        protected void onPostExecute(String result){
            builder.dismiss();
            SignInButton.setEnabled(true);
            GuestButton.setEnabled(true);
            if (myName.equals(myCheck)){
                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", myName);
                    loginPrefsEditor.putString("password", myPassword);
                    loginPrefsEditor.apply();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.apply();
                }
                createMyName(myName);
                Toast.makeText(SignIn.this, getResources().getString(R.string.loginS), Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(SignIn.this, SelectCampus.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable(SER_KEY, myFileIO);
                myIntent.putExtras(myBundle);
                SignIn.this.startActivity(myIntent);
            } else {Toast.makeText(SignIn.this, getResources().getString(R.string.incorrect), Toast.LENGTH_SHORT).show();}
        }
    }
}
