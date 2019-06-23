package hit.TechNews.Video;

import java.io.Serializable;
import java.util.List;

import hit.TechNews.Entry.VideoEntry;

public class VideoResult implements Serializable {
    private int code;
    private String error;
    private List<VideoEntry> videos;

    public List<VideoEntry> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoEntry> videos) {
        this.videos = videos;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
