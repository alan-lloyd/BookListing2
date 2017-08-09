package androidbasicsnd.lloyd.alan.com.udacity.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);
        //get received google book data values as Strings
        String publishedyear = currentBook.getPublishedYear();
        String title = currentBook.getTitle();
        String subtitle = currentBook.getSubtitle();
        String author = currentBook.getAuthor();
        //put collected data into App display page
        TextView publishedYearView = (TextView) listItemView.findViewById(R.id.published_year);
        publishedYearView.setText(publishedyear);

        TextView bookTitleView = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitleView.setText(title);

        TextView bookSubtitleView = (TextView) listItemView.findViewById(R.id.book_subtitle);
        bookSubtitleView.setText(subtitle);

        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(author);

        return listItemView;
    }//end of method getView()

}