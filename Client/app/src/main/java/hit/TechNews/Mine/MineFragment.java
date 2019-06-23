package hit.TechNews.Mine;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.MainActivity;
import hit.TechNews.R;
import hit.TechNews.Utils.ToastUtil;

public class MineFragment extends BaseFragment {
    UserEntry userEntry;
    @BindView(R.id.usernickname)
    TextView textView;
    @BindView(R.id.content)
    LinearLayout linearLayout;


    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void returnResult(String result) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view) {
        userEntry= DB.getInstance(App.instance).getUser();
        textView.setText(userEntry.getNickname());
    }

    @Override
    protected void lazyFetchData() {

    }


    public static MineFragment getInstance(){
        MineFragment mineFragment=new MineFragment();
        mineFragment.setArguments(new Bundle());
        return mineFragment;
    }
    @OnClick({R.id.setting,R.id.attention,R.id.track,R.id.avatar,R.id.userinfo,R.id.night,R.id.content,R.id.collect_article})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.setting:
//                        startActivity(SettingActivity.class);
                        break;
                    case R.id.attention:{
                        Intent intent=new Intent(getActivity(),TrackActivity.class);
                        intent.putExtra("type","收藏");
                        intent.putExtra("URL", api.IPADDRESS+"/api/collect");
                        intent.putExtra("key","");
                        startActivity(intent);}
                        break;
                    case R.id.track:{
                        Intent intent=new Intent(getActivity(),TrackActivity.class);
                        intent.putExtra("type","影迹");
                        intent.putExtra("URL", api.IPADDRESS+"/api/track");
                        intent.putExtra("key","");
                        startActivity(intent);
                        break;}
                    case R.id.night:{
                        ((MainActivity) Objects.requireNonNull(getActivity())).uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
//                        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                        if (mode == Configuration.UI_MODE_NIGHT_YES) {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                        } else if (mode == Configuration.UI_MODE_NIGHT_NO) {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                        }
//                        Objects.requireNonNull(getActivity()).recreate();
                        break;}
                    case R.id.avatar:
                        break;
                    case R.id.userinfo:{
                        if (userEntry.getUserid()==0){
                            new ToastUtil().shortDuration("请登录").show();
                        }
                        else{
                            Intent intent=new Intent(getActivity(),UserCenter.class);
                            intent.putExtra("user",userEntry);
                            startActivityForResult(intent,0);
                        }
                        break;}
                    case R.id.content:{
                        if (userEntry.getUserid()==0)
                            startActivityForResult(LoginActivity.class,1);
                        else {
                            startActivity(LoginActivity.class);
                        }
                        break;}
                    case R.id.collect_article:
                    {
                        Intent intent=new Intent(getActivity(),TrackActivity.class);
                        intent.putExtra("type","收藏");
                        intent.putExtra("URL", api.IPADDRESS+"/api/collect");
                        intent.putExtra("key","");
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userEntry=DB.getInstance(App.instance).getUser();
        textView.setText(userEntry.getNickname());

    }
}
