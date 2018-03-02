package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.ArticleActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving article data from the Guardian API.
 */

public class QueryUtils {

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int THREAD_SLEEP = 2000;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP response to the given URL and return a String as a response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the url is null, return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the response is successful (Response Code 200),
            // read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from parsing a JSON response
     */
    private static List<Article> extractFeatureFromJson(String articleJSON) {
        // If the JSON string is empty or null, return early
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList to start adding articles to.
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response String. If there's a problem with the way it's formatted,
        // a JSONException exception object will be thrown.
        try {
            // Create a JSON object from the JSON response string.
            JSONObject responseString = new JSONObject(articleJSON);
            // Extract the JSONObject associated with the key "response"
            JSONObject response = responseString.getJSONObject("response");
            // Extract the JSON Array associated with the key "results",
            // which represents a list of the articles.
            JSONArray articleArray = response.getJSONArray("results");
            // For each article in the array, create an {@link Article} object.
            for (int i = 0; i < articleArray.length(); i++) {
                // Get a single article at position [i] from the list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);
                // Extract the value for the key called "webTitle".
                String title = currentArticle.getString("webTitle");
                // Extract the value for the ey called "sectionName".
                String section = currentArticle.getString("sectionName");
                // Extract the value for the key called "webPublicationDate"
                String date = currentArticle.getString("webPublicationDate");
                // extract the value for the key called "webUrl"
                String url = currentArticle.getString("webUrl");

                Article article = new Article(title, section, date, url);
                articles.add(article);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // return the list of articles
        return articles;
    }

    /**
     * Query the Guardian  and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        //Pause execution for 2000 milliseconds so we are able to see the loading indicator
        try {
            Thread.sleep(THREAD_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Article> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return articles;
    }
}
