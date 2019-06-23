package hit.TechNews.Video;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_VideoImage;

import butterknife.BindView;
import hit.TechNews.R;
import hit.TechNews.Tools.HideControl;

public class VideoViewHolder extends AAH_CustomViewHolder {
    private SparseArray<View> mViews;
    private View mItemView;
    private HideControl hideControl=new HideControl();

//    @BindView(R.id.video_iv)
//    ImageView iv_video;
    @BindView(R.id.video_title)
    TextView title;
    @BindView(R.id.author_image)
    ImageView authorhead;
    @BindView(R.id.video_author)
    TextView author;
    @BindView(R.id.video_watch2)
    TextView watch_tv;
    @BindView(R.id.videoiv)
    AAH_VideoImage videoImage;
    @BindView(R.id.share_video)
    ImageView share;
    @BindView(R.id.thumb)
    ImageView thumb;
    @BindView(R.id.img_vol)
    ImageView img_vol;
    @BindView(R.id.img_playback)
    ImageView img_playback;
    boolean isMuted;
    VideoViewHolder(View x) {
        super(x);
        mViews = new SparseArray<>();
//        iv_video=x.findViewById(video_iv);
        mItemView=x;
        img_playback=getView(R.id.img_playback);
        img_vol=getView(R.id.img_vol);
        share=getView(R.id.share_video);
        thumb=getView(R.id.thumb);
        authorhead=x.findViewById(R.id.author_image);
        videoImage=getView(R.id.videoiv);

    }
    public VideoViewHolder setText(int viewId, int resId) {
        TextView textView = getView(viewId);
        textView.setText(String.valueOf(resId));
        return this;
    }
    public VideoViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }
    <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        T view1;
        view1 = (T) view;
        return view1;
    }

    @Override
    public void videoStarted() {
        super.videoStarted();
//        hideControl.setView(img_playback).startHideTimer();
        img_playback.setImageResource(R.drawable.baseline_pause_circle_filled_24);
        if (isMuted) {
            muteVideo();
            img_vol.setImageResource(R.drawable.baseline_volume_off_24);
        } else {
            unmuteVideo();
            img_vol.setImageResource(R.drawable.baseline_volume_up_24);
        }
    }

    @Override
    public void playVideo() {
        super.playVideo();
//        hideControl.startHideTimer();

    }

    @Override
    public void pauseVideo() {
        super.pauseVideo();
        img_playback.setImageResource(R.drawable.baseline_play_circle_filled_24);
    }

}
