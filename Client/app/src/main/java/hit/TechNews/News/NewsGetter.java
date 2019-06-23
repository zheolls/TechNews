package hit.TechNews.News;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import hit.TechNews.API.api;
import hit.TechNews.Entry.NewsEntry;
import hit.TechNews.Entry.ResultsEntry;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Utils.DateUtil;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.JsonUtil;
import hit.TechNews.Utils.ToastUtil;

public class NewsGetter {
    private String URL= api.IPADDRESS+api.getnews;
    private String TAG="NEWSGETTER";
    private String KEY="";
    private ResultsEntry resultsEntry;
    private List<NewsEntry> newsEntryList;
//    public NewsGetter(View view){
//        this.view=view;
//    }
    public NewsGetter(){

    }
    public NewsGetter setURL(String URL){
        this.URL=URL;
        return this;
    }

    public NewsGetter setKEY(String key){
        this.KEY=key;
        return this;
    }

    public void getNews(UserEntry user,int webid, int pages, String types, final NewsList newsList){
        List<HttpUtil.Param> params = new ArrayList<>();
        params.add(new HttpUtil.Param("userid",String.valueOf(user.getUserid())));
        params.add(new HttpUtil.Param("page",String.valueOf(pages)));
        params.add(new HttpUtil.Param("type",types));
        params.add(new HttpUtil.Param("webid",String.valueOf(webid)));
        params.add(new HttpUtil.Param("token",user.getToken()));
        params.add(new HttpUtil.Param("key",KEY));
        if (pages!=1 && resultsEntry!=null){
            NewsEntry newsEntry=resultsEntry.getNews().get(0);
            params.add(new HttpUtil.Param("date",newsEntry.getDate()));
        }else
        {
            params.add(new HttpUtil.Param("date", DateUtil.getFullDate()));
        }

        HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {

            @Override
            public void onSuccess(String response) {
                try {
                    Log.d(TAG,response);
                    resultsEntry= JsonUtil.deserialize(response,ResultsEntry.class);
                    if (resultsEntry.getCode()==8) {
                        newsEntryList= resultsEntry.getNews();
                        newsList.onSuccess(newsEntryList,8);

                    }
                    else if (resultsEntry.getCode()==4){
                        newsList.onSuccess(null,4);
                    }
                    else if (resultsEntry.getCode()==9){
                        newsList.onSuccess(null,9);
                    }
                    else {
                        Log.d(TAG,"ERROR");
                        new ToastUtil().shortDuration("未知错误").show();
                    }
                }catch (JsonSyntaxException  e){
                    Log.d(TAG,e.getMessage());
                }
            }
            @Override
            public void onFailure(Exception e) {
                newsList.onFailure(e);
            }
        }, params);
    }
    public interface NewsList{
        void onSuccess(List<NewsEntry> newsEntryList,int code);
        void onFailure(Exception e);
    }
}
