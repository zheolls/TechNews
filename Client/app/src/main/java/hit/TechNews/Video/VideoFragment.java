package hit.TechNews.Video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Entry.VideoEntry;
import hit.TechNews.R;
import hit.TechNews.Utils.ToastUtil;
import hit.TechNews.Views.EndLessOnScrollListener;
import hit.TechNews.Widget.RecycleViewDivider;

public class VideoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, VideoGetter.VideoList {
    private String TAG="VideoFragment";
    private List<VideoEntry> videoEntryList=new ArrayList<>();
    private VideoGetter videoGetter=new VideoGetter();
    private VideoGetter.VideoList videoList=this;

    @BindView(R.id.rv_video)
    AAH_CustomRecyclerView recyclerView;
    @BindView(R.id.videorefreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.floatBtn)
    FloatingActionButton floatingActionButton;

    private int pageIndex = 1;
    private VideoAdapter mAdapter;
    private UserEntry userEntry;
    LinearLayoutManager manager;
    private int code=0;

    public VideoFragment() {
    }

    public static VideoFragment getInstance(){
        VideoFragment videoFragment=new VideoFragment();
        videoFragment.setArguments(new Bundle());

        return videoFragment;
    }
    @Override
    public void onRefresh() {
        pageIndex=1;
        code=0;
        userEntry= DB.getInstance(App.instance).getUser();
        videoGetter.getVideos(userEntry,pageIndex,videoList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showErrorTip(String msg) {
        showErrorHint(msg);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void returnResult(String result) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView(View view) {
        userEntry= DB.getInstance(getActivity()).getUser();
        manager=new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRefreshLayout.setOnRefreshListener(this);
        Picasso picasso = Picasso.get();
        mAdapter=new VideoAdapter(videoEntryList, picasso);
        recyclerView.setActivity(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new RecycleViewDivider(getContext(),LinearLayoutManager.VERTICAL,50,getResources().getColor(R.color.LightGrey)));
        recyclerView.setPlayOnlyFirstVideo(true);
        recyclerView.setVisiblePercent(50);
//        recyclerView.preDownload(urls);
        recyclerView.setAdapter(mAdapter);
        mAdapter.addFooterView();
        recyclerView.setCheckForMp4(false);
        recyclerView.smoothScrollBy(0,1);
        recyclerView.smoothScrollBy(0,-1);
        recyclerView.addOnScrollListener(new EndLessOnScrollListener(manager) {
            @Override
            public void onLoadMore() {
                Log.d(TAG,String.valueOf(code));
                if (code==4){
                    new ToastUtil().shortDuration("没有更多内容").show();
                    mAdapter.setFooterVisible(View.GONE);

                }
                else {
                    pageIndex+=1;
                    if(videoEntryList.size()!=0){
                        mAdapter.setFooterVisible(View.VISIBLE);
                        videoGetter.getVideos(userEntry,pageIndex,videoList);
                    }
                }
            }
        });
        floatingActionButton.setOnClickListener(v -> {slideToTop();});

    }

    @Override
    protected void lazyFetchData() {
        mRefreshLayout.setRefreshing(true);
        if (code!=4){
            videoGetter.getVideos(userEntry,pageIndex,videoList);
        }
    }

    @Override
    public void onSuccess(List<VideoEntry> videoEntryList, int code) {
        if (code==4 ){
            mAdapter.setFooterVisible(View.GONE);
            this.code=code;
            return;
        }
        Log.d(TAG,"PAGE:"+String.valueOf(pageIndex));
        if (pageIndex==1){
            try {
                mAdapter.replaceAll(videoEntryList);
            }catch (Exception e ){
                Log.d(TAG,e.getMessage());
            }
        }
        else {
            List<VideoEntry> entryList=new ArrayList<>();
            for (VideoEntry videoEntry :videoEntryList){
                boolean flag=true;
                for (int i = 0; i < mAdapter.getItemCount()-1; i++)
                    if (videoEntry.getVideoid() == mAdapter.getVideoID(i)) {
                        flag = false;
                        break;
                    }
                if (!flag) {
                    continue;
                }
                entryList.add(videoEntry);
            }
            mAdapter.addAll(entryList);
        }
        mRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onFailure(Exception e) {
        mRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerView.stopVideos();
    }

    public void slideToTop() {
        recyclerView.smoothScrollToPosition(0);
    }
}
