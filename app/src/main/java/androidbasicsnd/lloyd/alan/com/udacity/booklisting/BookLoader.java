package androidbasicsnd.lloyd.alan.com.udacity.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

// loads a list of books by using an AsyncTask to perform the network request to the given URL
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // query URL
    private String mUrl;

    // Constructs a new BookLoader. context of the activity,
    // url to load data from
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //on a background thread
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // perform the network request, parse response, and extract list of books
        return QueryUtils.fetchBookData(mUrl);
    }
}