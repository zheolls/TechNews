package hit.TechNews.Entry;

import java.io.Serializable;
import java.util.List;

public class ResultsEntry implements Serializable {
    private List<NewsEntry> news;
    private int code;
    private String error;
    public List<NewsEntry> getNews() {
        return news;
    }

    public void setNews(List<NewsEntry> news) {
        this.news = news;
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
