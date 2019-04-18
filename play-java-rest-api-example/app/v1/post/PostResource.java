package v1.post;

/**
 * Resource for the API.  This is a presentation class for frontend work.
 */
public class PostResource {
    private String id;
    private String link;
    private String title;
    private String body;

    public PostResource() {
    }

    public PostResource(String id, String link, String title, String body) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.body = body;
    }

    public PostResource(PostData data, String link) {
        this.id = data.id.toString();
        this.link = link;
        this.title = data.title;
        this.body = data.body;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

}
