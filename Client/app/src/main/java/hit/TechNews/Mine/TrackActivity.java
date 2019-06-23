package hit.TechNews.Mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseActivity;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.NewsEntry;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.News.NewsAdapter;
import hit.TechNews.News.NewsDetailActivity;
import hit.TechNews.News.NewsGetter;
import hit.TechNews.R;
import hit.TechNews.Utils.ToastUtil;
import hit.TechNews.Views.EndLessOnScrollListener;
import hit.TechNews.Views.NewsItemDecoration;

public class TrackActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, NewsGetter.NewsList {

    Context context= App.instance;
    @BindView(R.id.basercview)
    RecyclerView mRecyclerView;
    @BindView(R.id.basesrview)
    SwipeRefreshLayout mRefreshLayout;
    NewsGetter.NewsList newsList=this;
    private NewsAdapter mAdapter;
    UserEntry userEntry;
    NewsGetter newsGetter = new NewsGetter();
    private int code=0;
    private int pageIndex = 1;
    private String KEY="";
    private String URL;
    String title;
    @BindView(R.id.basetv)
    TextView tv;
    @BindView(R.id.floatBtn)
    FloatingActionButton floatingActionButton;
    private List<NewsEntry> newDatas = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        assert getIntent() != null;
        title = getIntent().getStringExtra("type");
        URL=getIntent().getStringExtra("URL");
        KEY=getIntent().getStringExtra("key");
        tv.setText(title);
        userEntry= DB.getInstance(context).getUser();
        mRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new NewsItemDecoration(this));
        mAdapter = new NewsAdapter(newDatas, this);
        mAdapter.setOnItemClickLitener((i, baseViewHolder) -> {
            NewsEntry  newsEntry=newDatas.get(i);
            Intent intent= new Intent(this, NewsDetailActivity.class);
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
                        newsGetter
                                .setURL(URL)
                                .setKEY(KEY)
                                .getNews(userEntry,0,pageIndex, DB.getInstance(context).getSortType(),newsList);
                    }
                }

            }
        });
        floatingActionButton.setOnClickListener(v -> {
            mRecyclerView.smoothScrollToPosition(0);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        newsGetter
                .setURL(URL)
                .setKEY(KEY)
                .getNews(userEntry,0,pageIndex,DB.getInstance(context).getSortType(),newsList);
    }

    @Override
    public void onRefresh() {
        pageIndex=1;
        code=0;
        userEntry=DB.getInstance(context).getUser();
        newsGetter
                .setURL(URL)
                .setKEY(KEY)
                .getNews(userEntry,0,pageIndex,DB.getInstance(context).getSortType(),newsList);
    }

    @Override
    public void onSuccess(List<NewsEntry> newsEntryList, int code) {
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
    @Override
    public void onResume() {
        userEntry=DB.getInstance(context).getUser();
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
