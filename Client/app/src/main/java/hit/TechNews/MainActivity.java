package hit.TechNews;

import android.app.UiModeManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import hit.TechNews.BaseModel.BaseActivity;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.BaseModel.BaseFragmentAdapter;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Mine.MineFragment;
import hit.TechNews.News.NewsFragment;
import hit.TechNews.Video.VideoFragment;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private String TAG="MainActivity";
    private BaseFragment[] fragments;
    private String[] mTitles={"科技"};
    int currentTabPosition = 0;
    private UserEntry userEntry;
    public static final String CURRENT_TAB_POSITION = "HOME_CURRENT_TAB_POSITION";
    @BindView(R.id.journalism)
    Button mNews;
    @BindView(R.id.video)
    Button mVideo;
    @BindView(R.id.mine)
    Button mMine;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    public UiModeManager uiModeManager=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);
        uiModeManager=(UiModeManager)getSystemService(Context.UI_MODE_SERVICE);
        findViewById(R.id.night).setOnClickListener(v -> {
            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
        });

        if (savedInstanceState != null) {
            currentTabPosition = savedInstanceState.getInt(CURRENT_TAB_POSITION);
            mViewPager.setCurrentItem(currentTabPosition);
        }

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    private void resetTab() {
        mNews.setSelected(false);
        mVideo.setSelected(false);
        mMine.setSelected(false);
    }
    @Override
    public void onPageSelected(int i) {
        resetTab();
        switch (i) {
            case 0:
                mNews.setSelected(true);
                break;
            case 1:
                mVideo.setSelected(true);
                break;
            case 2:
                mMine.setSelected(true);
                break;
            default:
                //其他
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        uiModeManager=(UiModeManager)getSystemService(Context.UI_MODE_SERVICE);
        userEntry=DB.getInstance(App.instance).getUser();
        Log.d(TAG,userEntry.getNickname());
//        mTitles = getResources().getStringArray(R.array.main_titles);
        fragments = new BaseFragment[3];
        fragments[0] = NewsFragment.getInstance();
        fragments[1] = VideoFragment.getInstance();
        fragments[2] = MineFragment.getInstance();
        BaseFragmentAdapter mAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        if (mViewPager==null){
            Log.d(TAG,"NULL");
        }
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mNews.setSelected(true);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_TAB_POSITION, currentTabPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onBackPressed();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //仅当activity为task根才生效
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @OnClick({R.id.journalism,R.id.video,R.id.mine})
    public void OnViewClicked(View view){
        switch (view.getId()){
            case R.id.journalism:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.video:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.mine:
                mViewPager.setCurrentItem(2, false);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onResume() {
        userEntry=DB.getInstance(App.instance).getUser();
        super.onResume();
    }

}

