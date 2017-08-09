package androidbasicsnd.lloyd.alan.com.udacity.booklisting;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import android.app.LoaderManager.LoaderCallbacks;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    //Constant value for the Book loader ID. Can choose any integer
    private static final int BOOK_LOADER_ID = 1;
    // Tag for log messages
    private static final String LOG_TAG = BookLoader.class.getName();
    // two of three strings to build userSearchField at on Button Click();
    private static final String ADDITIONAL_SEARCH_PARAMETERS = "&title&maxResults=20&orderBy=newest";
    private static final String SEARCH_PREFIX = "https://www.googleapis.com/books/v1/volumes?q=";
    // URL for Book data from the Google Books API dataset
    public static String BOOKS_REQUEST_URL = "";

    private android.widget.SearchView searchText;
    //TextView displayed when list empty
    private TextView emptyStateTextView;
    // Adapter for the list of Books
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the  ListView in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);
        // Create a new adapter that takes an empty list of Books as input
        adapter = new BookAdapter(this, new ArrayList<Book>());
        // Set the adapter on the  ListView so the list can be populated in the user interface
        bookListView.setAdapter(adapter);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyStateTextView);
        searchText = (android.widget.SearchView) findViewById(R.id.search_text);

        getLoaderManager().initLoader(BOOK_LOADER_ID, null, MainActivity.this);

        //listener to load user selected web pages from book items
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Book that was clicked on
                Book currentBook = adapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());
                // Create a new intent to view the Book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                // Send the intent to launch a new activity
                //check device has an App that can handle this program, to stop it crashing
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);  //where intent is your intent
                }
            }
        });  //end of on click listener for user to view selected webpages of results

    }//end of onCreate


    public void onButtonClick(View view) {
        //collect user input for an online book search; replaces spaces with +
        String userSearchField = searchText.getQuery().toString().replaceAll(" ", "+");
        userSearchField.replaceAll(" ", ""); //removes unwanted empty txt or + symbol from end
        BOOKS_REQUEST_URL = SEARCH_PREFIX + userSearchField + ADDITIONAL_SEARCH_PARAMETERS;

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
        } else {
            if (networkInfo.isConnected()) {
                Log.i(LOG_TAG, "CONNECTION WORKS ");
                getLoaderManager().initLoader(BOOK_LOADER_ID, null, MainActivity.this);
            } else {
                //Update empty state with no connection error message
                emptyStateTextView.setText(R.string.no_internet_connection);
            }
        }
    } //end of onButtonClick

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, BOOKS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No books found."
        emptyStateTextView.setText(R.string.no_books);
        // Clear the adapter of previous Book data
        adapter.clear();
        // If there is a valid list of  Books, then add them to the adapter's
        // data set. This will trigger the ListView to update
        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, to clear out our existing data
        adapter.clear();
    }
} //end of MainActivity

