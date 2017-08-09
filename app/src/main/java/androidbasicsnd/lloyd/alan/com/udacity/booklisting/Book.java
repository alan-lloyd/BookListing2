package androidbasicsnd.lloyd.alan.com.udacity.booklisting;

public class Book {
    private String publishedYear;
    private String title;
    private String subTitle;
    private String author;
    private String url;

    public Book(String publishedYear, String author, String title, String subtitle, String url) {
        this.publishedYear = publishedYear;
        this.author = author;
        this.title = title;
        this.subTitle = subtitle;
        this.url = url;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

}