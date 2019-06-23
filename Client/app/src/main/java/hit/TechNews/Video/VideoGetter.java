package hit.TechNews.Video;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import hit.TechNews.API.api;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Entry.VideoEntry;
import hit.TechNews.Utils.DateUtil;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.JsonUtil;

public class VideoGetter {
    private String TAG="VideoGetter";
    private String URL= api.IPADDRESS+api.videos;
    private VideoResult videoResult;
    private List<VideoEntry> videoEntryList;
    public interface VideoList{
        void onSuccess(List<VideoEntry> newsEntryList, int code);
        void onFailure(Exception e);
    }
    public VideoGetter(){

    }
    public void  getVideos(UserEntry user,int pages,final VideoList videoList){
        List<HttpUtil.Param> params = new ArrayList<>();
        params.add(new HttpUtil.Param("userid",String.valueOf(user.getUserid())));
        params.add(new HttpUtil.Param("page",String.valueOf(pages)));
        params.add(new HttpUtil.Param("token",user.getToken()));
        if (pages!=1 && videoResult!=null){
            VideoEntry videoEntry=videoResult.getVideos().get(0);
            params.add(new HttpUtil.Param("date",videoEntry.getDate()));
        }else
        {
            params.add(new HttpUtil.Param("date", DateUtil.getFullDate()));
        }
        HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    videoResult= JsonUtil.deserialize(response,VideoResult.class);
                    if (videoResult.getCode()==8) {
                        Log.d(TAG,response);
                        videoEntryList= videoResult.getVideos();
                        Log.d(TAG,videoResult.getVideos().toString());
                        videoList.onSuccess(videoEntryList,8);

                    }
                    else if (videoResult.getCode()==4){
                        videoList.onSuccess(null,4);
                    }
                    else {
                        Log.d(TAG,"ERROR");
                    }
                }catch (JsonSyntaxException e){
                    Log.d(TAG,e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                videoList.onFailure(e);
            }
        },params);
    }

}
