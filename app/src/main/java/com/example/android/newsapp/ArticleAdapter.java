package com.example.android.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by georgeampartzidis on 16/7/17.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    private static final String LOG_TAG = ArticleAdapter.class.getName();

    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        //Initializing the Adapter's internal storage for the context and the list.
        //Calling the superclass constructor. We are passing 0 as the second argument since we will
        //be generating and using a custom listView.
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check whether the current view is used, otherwise inflate it
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get the data associated with the specified position
        Article currentArticle = getItem(position);

        // Find the TextView with id title and display the title in it
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentArticle.getnTitle());

        // Find the TextView with id section and display the section in it
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentArticle.getnSection());


        // Find the TextView with id date and display the date in it
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(formatDate(currentArticle.getnDate()));

        return listItemView;
    }

    // Method that formats the date to a string
    public String formatDate(String dateString) {

        String dateFormatted = "";
        String inputDate = dateString.substring(0, 10);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat finalFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date dt = inputFormat.parse(inputDate);
            dateFormatted = finalFormat.format(dt);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Exception in parsing the date data");
        }

        return dateFormatted;
    }
}
