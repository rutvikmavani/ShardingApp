package ShardingApp.Model;

public class Article {
    private String title;
    private long ns;
    private long id;
    private boolean redirect;
    private Revision revision;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getNs() {
        return ns;
    }

    public void setNs(long ns) {
        this.ns = ns;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", ns=" + ns +
                ", id=" + id +
                ", redirect=" + redirect +
                ", revision=" + revision +
                '}';
    }
}