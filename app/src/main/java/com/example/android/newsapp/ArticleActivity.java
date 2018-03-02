package com.example.android.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by georgeampartzidis on 16/7/17.
 */

public class ArticleActivity extends AppCompatActivity implements
        LoaderCallbacks<List<Article>> {

    //String for the query inserted by the user
    private String userQuery;

    private static final int ARTICLE_LOADER_ID = 1;
    public static final String LOG_TAG = ArticleActivity.class.getName();
    private static final String GUARDIAN_REQUEST = "https://content.guardianapis.com/search?q=";
    private static final String API_KEY = "&api-key=c4489f5e-8b37-4129-9ff9-eafcfd5324b2";

    /**
     * Adapter fot the list of articles
     */
    private ArticleAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * ProgressBar that is displayed when the app launches
     */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);

        // The following code allows to get the query from the Main Activity
        Intent intent = getIntent();
        userQuery = intent.getStringExtra("User query");

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes the list of articles as input. It is declared final
        // so that it can be accessed from the OnItemClickListener
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // set the adapter on the {@link ListView} to populate the list in the user interface.
        articleListView.setAdapter(mAdapter);

        // Set the view to show if the adapter is empty
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Set the view to show a ProgressBar at the beginning
        mProgressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Set an OnClickListener to get the url of the current article and
        // create a new Intent to view it.
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI Object (to pass it into the intent)
                Uri articleUri = Uri.parse(currentArticle.getnUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        if (isConnected) {
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            //if there is no connection, hide the loading indicator
            // and change the text message accordingly
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle bundle) {
        StringBuilder builder = new StringBuilder();
        builder.append(GUARDIAN_REQUEST);
        builder.append(userQuery);
        builder.append(API_KEY);
        String articleQuery = builder.toString();

        return new ArticleLoader(this, articleQuery);
    }


    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //Hide the Loading indicator as the data has been loaded
        mProgressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_data);
        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
