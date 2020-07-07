package ShardingApp.Model;

public class Revision {
    private long id;
    private String parentid;
    private long timestamp;
    private Contributor contributor;
    private String comment;
    private String model;
    private String format;
    private String text;
    private String sha1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @Override
    public String toString() {
        return "Revision{" +
                "id=" + id +
                ", parentid='" + parentid + '\'' +
                ", timestamp=" + timestamp +
                ", contributor=" + contributor +
                ", comment='" + comment + '\'' +
                ", model='" + model + '\'' +
                ", format='" + format + '\'' +
                ", sha1='" + sha1 + '\'' +
                '}';
    }
}