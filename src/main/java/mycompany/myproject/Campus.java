package mycompany.myproject;

import android.support.v7.app.AppCompatActivity;
import java.io.Serializable;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Name: Richard Clapham
 *
 * Name: Chandan Chugani
 *
 * Created by Rick & Chandan on 11/7/2015.
 * The campus object that holds all the information about a specific campus
 */
public class Campus extends AppCompatActivity implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private String CampusName;
    private String CampusDescription;
    private String CampusLocation;

    //Default Constructor
    public Campus() {
        this.CampusName = "";
        this.CampusDescription = "";
        this.CampusLocation = "";
    }

    public Campus(String salt1, String salt2, String salt3)
    {
        this.CampusName = salt1;
        this.CampusLocation = salt2;
        this.CampusDescription = salt3;
    }

    //My Setters
    public void setCampusName(String i){CampusName = i;}
    public void setCampusDescription(String i){CampusDescription = i;}
    public void setCampusLocation(String i){CampusLocation = i;}

    //My Getters
    public String getCampusName(){return CampusName;}
    public String getCampusDescription(){return CampusDescription;}
    public String getCampusLocation(){return CampusLocation;}
}