package hit.TechNews.News;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.BaseModel.BaseFragmentAdapter;
import hit.TechNews.Db.DB;
import hit.TechNews.MainActivity;
import hit.TechNews.Mine.TrackActivity;
import hit.TechNews.R;
import hit.TechNews.Utils.ToastUtil;

public class NewsFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    SearchView searchView;

    private String[] titles = {"IT之家","ZAKER","36kr","网易","雷锋网"};

    private static final String TAG = "NewsFragment";
    private BaseFragment[] mFragments;
    private int currentPosition = 0;


    public static NewsFragment getInstance() {
        NewsFragment newsFragment = new NewsFragment();
        newsFragment.setArguments(new Bundle());
        return newsFragment;
    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void returnResult(String result) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("MENU","dsfsd");
        inflater.inflate(R.menu.news_toolbar,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        TextView textView= (TextView) searchView
                .findViewById(
                        android.support.v7.appcompat.R.id.search_src_text
                );
        textView.setHintTextColor(getResources().getColor(R.color.white));
        textView.setTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent=new Intent(getActivity(), TrackActivity.class);
                intent.putExtra("URL", api.IPADDRESS+"/api/search");
                intent.putExtra("type","搜索结果");
                intent.putExtra("key",s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        mFragments=new BaseFragment[5];
        mFragments[0]=NewsClassfiFragment.newInstance(5);
        mFragments[1]=NewsClassfiFragment.newInstance(2);
        mFragments[2]=NewsClassfiFragment.newInstance(3);
        mFragments[3]=NewsClassfiFragment.newInstance(4);
        mFragments[4]=NewsClassfiFragment.newInstance(1);
        toolbar=view.findViewById(R.id.toolbar);
        mTabs.setTabMode(TabLayout.MODE_FIXED);
        BaseFragmentAdapter mAdapter = new BaseFragmentAdapter(getChildFragmentManager(), mFragments, titles);
        mViewPager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.search:
                    break;
                case R.id.night:{
                    if(((MainActivity)getActivity()).uiModeManager.getNightMode()== UiModeManager.MODE_NIGHT_YES)
                        ((MainActivity)getActivity()).uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
                    else ((MainActivity)getActivity()).uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                    break;}
                case R.id.sort:
                    String [] sorttype={"最新","最热","智能"};
                    int position= DB.getInstance(App.instance).getSort();
                    new AlertDialog.Builder(Objects.requireNonNull(getActivity())).setTitle("选择排序方式")
                            .setSingleChoiceItems(sorttype, position, (dialog, which) -> {
                                if (DB.getInstance(App.instance).getUser().getUserid()==0 && which==2){
                                    new ToastUtil().shortDuration("需要登录").show();
                                }
                                else if (which>=0 && which<3){
//                                    position=which;
                                    DB.getInstance(App.instance).setSortType(which);
                                }
                            })
                            .setPositiveButton("确定", (dialog, which) -> {
//                                DB.getInstance(App.instance).setSortType(which);
                                ((NewsClassfiFragment)mFragments[currentPosition]).onRefresh();
                            }).show();
                    break;

            }
            return false;
        });
        toolbar.setOnClickListener(v -> ((NewsClassfiFragment)mFragments[currentPosition]).slideToTop());
    }

    @Override
    protected void lazyFetchData() {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        currentPosition=i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

}
