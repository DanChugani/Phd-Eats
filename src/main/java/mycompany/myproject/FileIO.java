package mycompany.myproject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Team: Ch-ick
 * Project Name: PHD-Eats
 *
 * Date: 10/20/2015
 *
 * Created by:
 * Name: Richard Clapham
 * Name: Chandan Chugani
 *
 * Description:
 * An object that stores the users choices as well as an error check. For example it will
 * store a network check in certain areas of the app that way if the user looses network connection
 * part way through the app it will handle that and relaunch in a local mode.
 */

// fileName = "myFile.txt" or myFile
public class FileIO implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private boolean myNetworkState;
    private String myCampusChoice;
    private String myRestaurantChoice;

    //Default Constructor which is null
    public FileIO(){}

    // My setters
    public void setMyNetworkState(boolean i){myNetworkState = i;}
    public void setMyCampusChoice(String i){myCampusChoice = i;}
    public void setMyRestaurantChoice(String i){myRestaurantChoice = i;}

    //My getters
    public boolean getMyNetworkState(){return myNetworkState;}
    public String getMyCampusChoice(){return myCampusChoice;}
    public String getMyRestaurantChoice(){return myRestaurantChoice;}

    //Allows the app to write to a local file if needed not currently being used
    public static void writeAsObject(ArrayList<Restaurant> myTemp, String fileName) throws IOException, ClassNotFoundException
    {
        //Write data to File
        ObjectOutputStream myOutput = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        myOutput.writeObject(myTemp);
        myOutput.close();
    }

    //Reads from the local if being used
    public static ArrayList<Restaurant> readObject(String fileName) throws IOException, ClassNotFoundException
    {
        //Read data from File
        ObjectInputStream myInput = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
        ArrayList<Restaurant> myArrayList = (ArrayList<Restaurant>)myInput.readObject();
        //displayArray(inCompany);
        return myArrayList;
    }

    //Displays data for local file not being used
    public static void displayArray(ArrayList<Restaurant> myTemp)
    {
        System.out.println("\nDisplays the contents of the Array: \n");
        for(int count = 0; count < myTemp.size(); count++)
            System.out.println(myTemp.get(count));
    }
}
