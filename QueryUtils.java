package androidbasicsnd.lloyd.alan.com.udacity.booklisting;

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

// Helper methods related to requesting and receiving data from Google books.
public final class QueryUtils {


    private static final String LOG_TAG = QueryUtils.class.getSimpleName();   // Tag for log messages
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    //constants for data extracted from JSON data
    private static final String KEY_TITLE = "title";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_PUBLISHED_DATE = "publishedDate";
    private static final String KEY_VOLUME_INFO = "volumeInfo";


    /**
     * Create a private constructor because no one should ever create a  QueryUtils object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed)
     */
    private QueryUtils() {
    }

    // Query the Google Books API dataset and return a list of class Book objects
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of  Book items
        List<Book> books = extractFeatureFromJson(jsonResponse);
        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url=null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (http response code 200),
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server
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
     * Return a list of Book objects that has been built up from parsing the given JSON response
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList to start adding books to
        ArrayList<Book> books = new ArrayList<>();

        /** Try to parse the JSON response string. If there's a problem with the way the JSON is formatted,
         * a JSONException exception object will be thrown.
         *  Catch the exception so the app doesn't crash, and print the error message to the logs. */
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            // Getting JSON Array node
            JSONArray bookArray = baseJsonResponse.getJSONArray(KEY_ITEMS);
            // looping through all imported books
            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject eachbook = bookArray.getJSONObject(i);
                //discarding unwanted data
                JSONObject abook = eachbook.getJSONObject(KEY_VOLUME_INFO);
                // Extract the value for keys of interest
                String publishedyear = "";
                String subtitle = "";
                String title = "";
                String authors = "";

                //some books have no date key - try/catch handles this
                try {
                    publishedyear = abook.getString(KEY_PUBLISHED_DATE);
                } catch (JSONException e) {
                    publishedyear = "no publication date";
                }

                //some books have no subtitle key - try/catch handles this
                try {
                    subtitle = abook.getString(KEY_SUBTITLE);
                } catch (JSONException e) {
                    subtitle = "no subtitle";
                }


                //some books have no title key - try/catch handles this
                try {
                    title = abook.getString(KEY_TITLE);
                } catch (JSONException e) {
                    title = "no title ";
                }


                //some books have no authors key - try/catch handles this
                try {
                    authors = abook.getString(KEY_AUTHORS);
                } catch (JSONException e) {
                    authors = "no authors name";
                }


                // Extract the value for the key called "url"
                String url = abook.getString("canonicalVolumeLink");
                // Create a new object with class Book attributes, from JSON response
                Book thisBook = new Book(publishedyear, authors, title, subtitle, url);
                books.add(thisBook);
            }
        } catch (JSONException e) {
            // If another error is thrown when executing any of the above statements in the main "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Return the list of books
        return books;
    }// end of extractFeatureFromJson()
}