package mycompany.myproject;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

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
 * This is an adapter that will receive an object of Review and then parse the data accordingly in
 * the listview adapter. Simply receives an object and checks the information and applies the correct
 * data to the appropriate field assuming the data is not null.
 */

public class ReviewAdapter extends ArrayAdapter<Review>
{
    // declaring our ArrayList of items
    private ArrayList<Review> objects;

    /* here we must override the constructor for ArrayAdapter
    *  the only variable we care about now is ArrayList<Item> objects,
    *  because it is the list of objects we want to display.
    */
    public ReviewAdapter(Context context, int textViewResourceId, ArrayList<Review> objects)
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
            v = inflater.inflate(R.layout.review_view, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, we refer to the current Item as object.
		 */
        Review i = objects.get(position);

        if (i != null) {

            // This is how we obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.
            TextView t1 = (TextView) v.findViewById(R.id.textView4);
            RatingBar rB =(RatingBar) v.findViewById(R.id.ratingBar3);
            TextView t3 = (TextView) v.findViewById(R.id.textView6);

            if (t1 != null){
                t1.setText(i.getUserName());
            }
            if (rB != null){
                rB.setRating(i.getRating());
            }
            if (t3 != null){
                t3.setText(i.getDescription());
            }
        }
        // the view must be returned to our activity
        return v;
    }
}
