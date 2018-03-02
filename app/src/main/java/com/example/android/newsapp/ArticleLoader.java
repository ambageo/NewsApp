package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by georgeampartzidis on 17/7/17.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = ArticleLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Loading on a background thread
     */
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Otherwise perform the network request, parse the response
        // and return a list of the articles.
        List<Article> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
}