package mycompany.myproject;

import java.io.Serializable;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Date: 10/18/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Dan Chugani
 *
 * Description:
 * A user object designed to store the users information
 */
public class User implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String email;
    private String name;
    private String password;

    //Default Constructor
    public User()
    {
        email = "";
        name = "";
        password = "";
    }

    //My Setters
    public void setEmail(String i){email = i;}
    public void setName(String i){name = i;}
    public void setPassword(String i){password = i;}

    //My getters
    public String getEmail(){return email;}
    public String getName(){return name;}
    public String getPassword(){return password;}
}
