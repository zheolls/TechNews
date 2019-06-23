package hit.TechNews.Video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hit.TechNews.API.api;
import hit.TechNews.Entry.VideoEntry;
import hit.TechNews.R;
import hit.TechNews.Utils.HttpUtil;

public class VideoAdapter extends AAH_VideosAdapter   {
    private String TAG="VideoAdapter";
    private final List<VideoEntry> videoList;
    private final Picasso picasso;
    private Context context;
    private View footerView;
    private int footviewer;
    public VideoAdapter(List<VideoEntry> videolist,Picasso picasso){
        this.picasso=picasso;
        this.videoList=videolist;
    }

    @NonNull
    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        if (viewType==0){
            return new VideoViewHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_video_item,parent,false));
        }
        else {
            footerView=LayoutInflater
                    .from(parent.getContext())
                    .inflate(footviewer,parent,false);
            return new AAH_CustomViewHolder(footerView);
        }

    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!(footviewer != 0 && position == getItemCount()-1 )) {
            VideoViewHolder videoViewHolder=(VideoViewHolder)holder;
            videoViewHolder.setText(R.id.video_title,videoList.get(position).getTitle());
            videoViewHolder.setText(R.id.video_watch2,videoList.get(position).getFever());
            videoViewHolder.setText(R.id.video_author,videoList.get(position).getSource());
            videoViewHolder.getView(R.id.share_video).setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String URL="https://www.bilibili.com/av"+videoList.get(position).getAid()+"/";
                intent.putExtra(Intent.EXTRA_TEXT, videoList.get(position).getTitle()+" "+URL);
                intent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                context.startActivity(Intent.createChooser(intent, "分享到"));
            });
            videoViewHolder.videoImage.setOnClickListener(v -> {
                if(videoViewHolder.img_playback.getVisibility()==View.VISIBLE){
                    videoViewHolder.img_playback.setVisibility(View.INVISIBLE);
                    videoViewHolder.img_vol.setVisibility(View.INVISIBLE);

                }
                else {
                    videoViewHolder.img_playback.setVisibility(View.VISIBLE);
                    videoViewHolder.img_vol.setVisibility(View.VISIBLE);
                }

            });

            videoViewHolder.getView(R.id.img_playback).setOnClickListener(v -> {
                if(videoViewHolder.img_playback.getVisibility()==View.VISIBLE){
                    videoViewHolder.img_playback.setVisibility(View.INVISIBLE);
                    videoViewHolder.img_vol.setVisibility(View.INVISIBLE);

                }
                else {
                    videoViewHolder.img_playback.setVisibility(View.VISIBLE);
                    videoViewHolder.img_vol.setVisibility(View.VISIBLE);
                }

                if (holder.isPlaying()) {
                    holder.pauseVideo();
                    holder.setPaused(true);
                } else {
                    holder.playVideo();
                    holder.setPaused(false);
                }
            });
            ImageView img_vol=videoViewHolder.getView(R.id.img_vol);
            img_vol.setOnClickListener(v -> {
                if (videoViewHolder.isMuted) {
                    holder.unmuteVideo();
                   img_vol.setImageResource(R.drawable.baseline_volume_up_24);
                } else {
                    holder.muteVideo();
                    img_vol.setImageResource(R.drawable.baseline_volume_off_24);
                }
                videoViewHolder.isMuted = !videoViewHolder.isMuted;
            });

            if (videoList.get(position).getUrl() == null) {
                videoViewHolder.getView(R.id.img_vol).setVisibility(View.GONE);
                videoViewHolder.getView(R.id.img_playback).setVisibility(View.GONE);
            } else {
                videoViewHolder.getView(R.id.img_vol).setVisibility(View.VISIBLE);
                videoViewHolder.getView(R.id.img_playback).setVisibility(View.VISIBLE);
            }

            List<HttpUtil.Param> params=new ArrayList<>();
            videoViewHolder.getView(R.id.thumb).setOnClickListener(v ->{
                String URL= api.IPADDRESS+"/api/video_like";
                HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                },params);
            });

            if (videoList.get(position).getAuthorimgurl() != null&& !videoList.get(position).getAuthorimgurl().isEmpty())
                Glide.with(context)
                        .load(videoList.get(position).getAuthorimgurl())
                        .apply(RequestOptions.circleCropTransform())
                        .into((((VideoViewHolder) holder).authorhead));
            assert holder.getImageUrl() != null;
            Log.d(TAG,videoList.get(position).getImgurl());
            holder.setImageUrl(videoList.get(position).getImgurl());
            holder.setVideoUrl(videoList.get(position).getUrl());
            if (videoList.get(position).getImgurl()!=null && !videoList.get(position).getImgurl().isEmpty())
                picasso.load(videoList.get(position).getImgurl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (footviewer != 0 && position == getItemCount() - 1) {
            type = 1;
        } else {
            type = 0;
        }
        Log.d(TAG,String.valueOf(position));
        return type;
    }

    void addFooterView() {
        this.footviewer = R.layout.layout_footer;
        notifyItemInserted(getItemCount() - 1);
    }
    int getVideoID(int position){
        return videoList.get(position).getVideoid();
    }

    void setFooterVisible(int visible) {
        footerView.setVisibility(visible);
    }
    public View getFooterView(){
        return this.footerView;
    }
    void addAll(List<VideoEntry> elements) {
        videoList.addAll(elements);
        notifyDataSetChanged();
    }
    void replaceAll(List<VideoEntry> elements) {
        if (videoList.size() > 0) {
            videoList.clear();
        }
        videoList.addAll(elements);
        notifyDataSetChanged();
    }
}
