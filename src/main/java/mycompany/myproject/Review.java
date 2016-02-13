package mycompany.myproject;

import java.io.Serializable;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Date: 10/24/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Dan Chugani
 *
 * Description:
 * An Object class that is designed to store review information. The review object is used to store
 * the user submitted reviews and the reviews submitted from the remote database.
 */
public class Review implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String userName;
    private float rating;
    private String description;

    //Default Constructor
    public Review()
    {
        userName ="";
        rating=0;
        description="";
    }

    public Review(String salt1, float salt2, String salt3)
    {
        userName = salt1;
        rating = salt2;
        description = salt3;
    }

    //My Setters
    public void setUserName(String i){userName = i;}
    public void setRating(float i){rating = i;}
    public void setDescription(String i){description = i;}

    //My Getters
    public String getUserName(){return userName;}
    public float getRating(){return rating;}
    public String getDescription(){return description;}
}
