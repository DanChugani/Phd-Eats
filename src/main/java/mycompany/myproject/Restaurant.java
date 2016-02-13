package mycompany.myproject;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Date: 10/14/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Dan Chugani
 *
 * Description:
 * This class is used to create objects of a restaurants specific information.
 */
public class Restaurant extends AppCompatActivity implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String campusName;
    private String cDescription;
    private String cLocation;

    private String RestaurantName;
    private String RestaurantDescription;
    private String RestaurantLocation;
    private float RestaurantRating;
    private ArrayList<Review> RestaurantReviews;

    //Default Constructor
    public Restaurant()
    {
        this.campusName = "";
        this.cDescription = "";
        this.cLocation = "";
        this.RestaurantName = "";
        this.RestaurantDescription = "";
        this.RestaurantLocation = "";
        this.RestaurantRating = 0;
        RestaurantReviews = new ArrayList<Review>();
    }

    public Restaurant(String salt1, String salt2, String salt3, String salt4, String salt5, String salt6){
        this.campusName = salt1;
        this.cDescription = salt2;
        this.cLocation = salt3;
        this.RestaurantName = salt4;
        this.RestaurantDescription = salt5;
        this.RestaurantLocation = salt6;
        this.RestaurantRating = 0;
        RestaurantReviews = new ArrayList<Review>();
    }

    public Restaurant(String salt1, String salt2, String salt3){
        this.campusName = "";
        this.cDescription = "";
        this.cLocation = "";
        this.RestaurantName = salt1;
        this.RestaurantDescription = salt2;
        this.RestaurantLocation = salt3;
        this.RestaurantRating = 0;
        RestaurantReviews = new ArrayList<Review>();
    }

    public Restaurant(String salt1, String salt2, String salt3, String salt4){
        this.campusName = salt1;
        this.RestaurantName = salt2;
        this.RestaurantDescription = salt3;
        this.RestaurantLocation = salt4;
        this.RestaurantRating = 0;
        RestaurantReviews = new ArrayList<Review>();
    }

    //My Setters
    public void setRestaurantName(String i){RestaurantName = i;}
    public void setRestaurantDescription(String i){RestaurantDescription = i;}
    public void setRestaurantLocation(String i){RestaurantLocation = i;}
    public void setRestaurantRating(int i){RestaurantRating = i;}
    public void addReview(Review i){RestaurantReviews.add(i);}

    public void setCampus(String i){campusName = i;}
    public void setCDescription(String i){cDescription = i;}
    public void setCLocation(String i){cLocation = i;}

    /*
     * A function that will calculate the rating based on the amount of ratings the restaurant has
     */
    public void calculateRating() {
        float i= 0;
        for(int count = 0; count < getAmountofReviews(); count++){
            i = i + getReview(count).getRating();
        }
        i = i / getAmountofReviews();
        RestaurantRating = i;
    }

    //My Getters
    public String getCampus(){return campusName;}
    public String getCDescription(){return cDescription;}
    public String getCLocation(){return cLocation;}

    public String getRestaurantName(){return RestaurantName;}
    public String getRestaurantDescription(){return RestaurantDescription;}
    public String getRestaurantLocation(){return RestaurantLocation;}
    public float getRestaurantRating(){return RestaurantRating;}

    public Review getReview(int i){return RestaurantReviews.get(i);}
    public int getAmountofReviews(){return RestaurantReviews.size();}
}
