package hit.TechNews.News;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseActivity;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.NewsEntry;
import hit.TechNews.Entry.ResultsEntry;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.R;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.JsonUtil;
import hit.TechNews.Utils.ToastUtil;

public class NewsDetailActivity extends BaseActivity {
    private static final String TAG = "NewsDetailActivity";
    private String URL= api.IPADDRESS+api.collect_article;
    private boolean is_collect=false;
    NewsEntry newsEntry;
    UserEntry userEntry;
    @BindView(R.id.toolbar2)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webView;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_article;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        userEntry= DB.getInstance(App.instance).getUser();
        newsEntry=(NewsEntry)getIntent().getSerializableExtra("news");
        Collect_article(0);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.star:
                    Log.d("NEWS",String.valueOf(is_collect));
                        if (is_collect){
                            Collect_article(2);
                        }
                        else {
                            Collect_article(1);
                        }
                    break;
                case R.id.share:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, newsEntry.getTitle()+" "+newsEntry.getUrl());
                    intent.setType("text/plain");
                    //设置分享列表的标题，并且每次都显示分享列表
                    startActivity(Intent.createChooser(intent, "分享到"));
                    break;
            }
            return false;
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(newsEntry.getUrl());
    }

    private void Collect_article(int check){
        List<HttpUtil.Param> params=new ArrayList<>();
        params.add(new HttpUtil.Param("token",userEntry.getToken()));
        params.add(new HttpUtil.Param("userid",String.valueOf(userEntry.getUserid())));
        params.add(new HttpUtil.Param("articleid",String.valueOf(newsEntry.getArticleid())));
        params.add(new HttpUtil.Param("check",String.valueOf(check)));
        HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                ResultsEntry resultsEntry= JsonUtil.deserialize(response,ResultsEntry.class);
                Log.d("NEWS",String.valueOf(response));
                if (check==0){
                    if (resultsEntry.getCode()==0){
                        is_collect=true;
                        toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star));
                    }
                    else {
                        is_collect=false;
                        toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_border));
                    }

                }
                else if (check==1) {
                    if (resultsEntry.getCode()==0){
                        is_collect=true;
                        toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star));
                    }
                    else {
                        new ToastUtil().shortDuration("收藏失败").show();
                    }
                }
                else {
                    if (resultsEntry.getCode()==0){
                        is_collect=false;
                        toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_border));

                    }
                    else {
                        new ToastUtil().shortDuration("取消收藏失败").show();
                    }
                }

            }

            @Override
            public void onFailure(Exception e) {
                new ToastUtil().shortDuration("网络错误").show();

            }
        },params);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsEntry=(NewsEntry) getIntent().getSerializableExtra("news");
        userEntry = (UserEntry) getIntent().getSerializableExtra("user");
        toolbar.inflateMenu(R.menu.article);

    }
}
