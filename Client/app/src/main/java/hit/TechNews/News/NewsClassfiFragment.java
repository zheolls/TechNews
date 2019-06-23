package hit.TechNews.News;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.NewsEntry;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Mine.LoginActivity;
import hit.TechNews.R;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.ToastUtil;
import hit.TechNews.Views.EndLessOnScrollListener;
import hit.TechNews.Views.NewsItemDecoration;

public class NewsClassfiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,NewsGetter.NewsList {
    private String URL= api.IPADDRESS+api.article_click;
    Context context=App.instance;
    public static NewsClassfiFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        NewsClassfiFragment fragment = new NewsClassfiFragment();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mRefreshLayout;
    NewsGetter.NewsList newsList=this;
    private NewsAdapter mAdapter;
    UserEntry userEntry;
    NewsGetter newsGetter = new NewsGetter();
    private int code=0;
    private int pageIndex = 1;
    private int webid=1;
    private List<NewsEntry> newDatas = new ArrayList<>();
    @Override
    public void showErrorTip(String msg) {
        showErrorHint(msg);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void returnResult(String result) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getArguments() != null;
        webid=getArguments().getInt("type");
    }



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_classfi;
    }

    @Override
    protected void initView(View view) {
        userEntry= DB.getInstance(context).getUser();
        mRefreshLayout=view.findViewById(R.id.refreshLayout);
        mRecyclerView=view.findViewById(R.id.recyclerView);
        mRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new NewsItemDecoration(getActivity()));
        mAdapter = new NewsAdapter(newDatas, getActivity());
        mAdapter.setOnItemClickLitener((i, baseViewHolder) -> {
            NewsEntry  newsEntry=newDatas.get(i);
            Intent intent= new Intent(getActivity(), NewsDetailActivity.class);

            List<HttpUtil.Param> param=new ArrayList<>();
            param.add(new HttpUtil.Param("userid",String.valueOf(userEntry.getUserid())));
            param.add(new HttpUtil.Param("articleid",String.valueOf(newsEntry.getArticleid())));
            param.add(new HttpUtil.Param("token",userEntry.getToken()));
            HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
                @Override
                public void onSuccess(String response) {

                }

                @Override
                public void onFailure(Exception e) {

                }
            },param);
            intent.putExtra("news",newsEntry);
            intent.putExtra("user",userEntry);
            startActivity(intent);
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addFooterView(R.layout.layout_footer);
        mRecyclerView.addOnScrollListener(new EndLessOnScrollListener(manager) {
            @Override
            public void onLoadMore() {
                if (code==4){
                    new ToastUtil().shortDuration("没有更多内容").show();
                    mAdapter.setFooterVisible(View.GONE);
                }
                else {
                    pageIndex+=1;
                    if(newDatas.size()!=0){
                        mAdapter.setFooterVisible(View.VISIBLE);
                        newsGetter.getNews(userEntry,webid,pageIndex, DB.getInstance(context).getSortType(),newsList);
                    }
                }

            }
        });
    }

    @Override
    protected void lazyFetchData() {
        mRefreshLayout.setRefreshing(true);
        if (code!=4&& code!=9){
            userEntry=DB.getInstance(context).getUser();
            newsGetter.getNews(userEntry,webid,pageIndex,DB.getInstance(context).getSortType(),newsList);

        }
        else if (code==9) {
            startActivity(LoginActivity.class);
        }

    }

    @Override
    public void onRefresh() {
        pageIndex=1;
        code=0;
        userEntry=DB.getInstance(context).getUser();
        newsGetter.getNews(userEntry,webid,pageIndex,DB.getInstance(context).getSortType(),newsList);
    }

    @Override
    public void onSuccess(List<NewsEntry> newsEntryList,int code) {
        if (code==4){
            mAdapter.setFooterVisible(View.GONE);
            this.code=code;
            return;
        }
        else if (code==9) {
            Log.d("NEWSCLASS",String.valueOf(code));
            startActivity(LoginActivity.class);
            new ToastUtil().shortDuration("需要重新登录").show();

        }
        else {
            if (pageIndex==1){
                try {
                    mAdapter.replaceAll(newsEntryList);
                }catch (Exception e ){
                    Log.d("NEWS",e.getMessage());
                }

            }
            else {
                List<NewsEntry> entryList=new ArrayList<>();
                for (NewsEntry newsEntry :newsEntryList){
                    boolean flag=true;
                    for (int i = 0; i < mAdapter.getItemCount()-1; i++)
                        if (newsEntry.getArticleid() == mAdapter.getArticleId(i)) {
                            flag = false;
                            break;
                        }
                    if (!flag) {
                        continue;
                    }
                    entryList.add(newsEntry);
                }
                mAdapter.addAll(entryList);
            }
        }

        mRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onFailure(Exception e) {
        mRefreshLayout.setRefreshing(false);
    }
    public void slideToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onResume() {
        userEntry=DB.getInstance(context).getUser();
        super.onResume();

    }
}
