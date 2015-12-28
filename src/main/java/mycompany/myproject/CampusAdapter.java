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
 * Name: Richard Clapham
 * Student #: 821-490-125
 *
 * Name: Chandan Chugani
 * Student #: RefactorThisToYourStudent#Chandan
 *
 * Created by Rick on 10/31/2015.
 * This is an adapter for the select campus screen. It Recieves a campus and displays only the campus
 * name currently however it could easily be modified to show more information
 */
public class CampusAdapter extends ArrayAdapter<Campus>
{
// declaring our ArrayList of items
    private ArrayList<Campus> objects;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public CampusAdapter(Context context, int textViewResourceId, ArrayList<Campus> objects)
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
            v = inflater.inflate(R.layout.campus_view, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Campus i = objects.get(position);

        //Checks if object isn't null and if it isnt loads the data into the listview
        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.
            TextView t1 = (TextView) v.findViewById(R.id.textView4);


            if (t1 != null){
                t1.setText(i.getCampusName());
            }
        }
        // the view must be returned to our activity
        return v;
    }
}

