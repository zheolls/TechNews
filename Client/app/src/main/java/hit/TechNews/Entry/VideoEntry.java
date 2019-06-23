package hit.TechNews.Entry;

import java.io.Serializable;

public class VideoEntry implements Serializable {
    private int videoid;
    private String url;
    private String source;
    private String imgurl;
    private String date;
    private int fever;
    private String title;
    private String aid;
    private String authorimgurl;

    public int getVideoid() {
        return videoid;
    }

    public void setVideoid(int videoid) {
        this.videoid = videoid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getFever() {
        return fever;
    }

    public void setFever(int fever) {
        this.fever = fever;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthorimgurl() {
        return authorimgurl;
    }

    public void setAuthorimgurl(String authorimgurl) {
        this.authorimgurl = authorimgurl;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }
}
