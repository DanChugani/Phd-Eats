package mycompany.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
 * This is an adapter that will receive an object of Restaurant and then parse the data accordingly
 * in to the listview adapter. Simply receives an object and checks for null values
 * then applies the correct data to the appropriate textfield.
 */
public class RestaurantAdapter extends ArrayAdapter<Restaurant>
{
    // declaring our ArrayList of items
    private ArrayList<Restaurant> objects;

    /* here we override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public RestaurantAdapter(Context context, int textViewResourceId, ArrayList<Restaurant> objects)
    {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.restaurant_view, null);
        }

		/*
		 * The variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, we refer to the current Item as object.
		 */
        Restaurant i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.
            TextView t1 = (TextView) v.findViewById(R.id.textView4);
            TextView t2 = (TextView) v.findViewById(R.id.textView5);
            TextView t3 = (TextView) v.findViewById(R.id.textView6);

            if (t1 != null){
                t1.setText(i.getRestaurantName());
            }
            if (t2 != null){
                t2.setText(i.getRestaurantLocation());
            }
            if (t3 != null){
                t3.setText(i.getRestaurantDescription());
            }
        }
        // the view must be returned to our activity
        return v;
    }
}
