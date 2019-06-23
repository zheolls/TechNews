package hit.TechNews.Mine;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import hit.TechNews.API.api;
import hit.TechNews.App;
import hit.TechNews.BaseModel.BaseActivity;
import hit.TechNews.Db.DB;
import hit.TechNews.Entry.UserEntry;
import hit.TechNews.Mine.UserInfo.ResultCode;
import hit.TechNews.Mine.UserInfo.UserInfoAdapter;
import hit.TechNews.Mine.UserInfo.UserInfoItem;
import hit.TechNews.R;
import hit.TechNews.Utils.HttpUtil;
import hit.TechNews.Utils.JsonUtil;
import hit.TechNews.Utils.ToastUtil;

public class UserCenter extends BaseActivity {
    private UserInfoAdapter adapter;
    private UserEntry userEntry;

    @BindView(R.id.user_info_toolbar)
    Toolbar toolbar;

    @BindView(R.id.userlist)
    ListView listView;

    private ResultCode resultCode;

    private ArrayList<UserInfoItem> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_userinfo);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void initView() {
        arrayList.clear();
        userEntry=DB.getInstance(App.instance).getUser();
        arrayList.add(new UserInfoItem("ID",String.valueOf(userEntry.getUserid())));
        arrayList.add(new UserInfoItem("邮箱",userEntry.getEmail()));
        arrayList.add(new UserInfoItem("电话",userEntry.getPhone()));
        arrayList.add(new UserInfoItem("昵称",userEntry.getNickname()));
        adapter=new UserInfoAdapter(UserCenter.this,R.layout.user_info_item,arrayList);
        listView.setAdapter(adapter);
        toolbar.inflateMenu(R.menu.user_center_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.save:

                        Savechanges();
                        break;
                    case R.id.edit:
                        arrayList.get(2).setEnable(true);
                        arrayList.get(3).setEnable(true);
                        adapter.notifyDataSetChanged();
                        toolbar.getMenu().getItem(0).setVisible(true);
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(false);
                        break;
                    case R.id.exit:
                        Logout();
                        break;
                }
                return false;
            }
        });
    }
    private void Logout(){
        String URL=api.IPADDRESS+"/api/logout";
        List<HttpUtil.Param> params=new ArrayList<>();
        params.add(new HttpUtil.Param("token",userEntry.getToken()));
        HttpUtil.post(URL, new HttpUtil.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                DB.getInstance(App.instance).Logout();
                new ToastUtil().shortDuration("已注销").show();
                finish();
            }
            @Override
            public void onFailure(Exception e) { }
        },params);
    }
    private void Savechanges(){
        final String phone=((EditText)(listView.getChildAt(2).findViewById(R.id.user_center_et))).getText().toString();
        final String nickname=((EditText)(listView.getChildAt(3).findViewById(R.id.user_center_et))).getText().toString();
        if (phone.equals(userEntry.getPhone()) && nickname.equals(userEntry.getNickname())){
            return;
        }
        List<HttpUtil.Param> params=new ArrayList<>();
        params.add(new HttpUtil.Param("userid",String.valueOf(userEntry.getUserid())));
        params.add(new HttpUtil.Param("phone",phone));
        params.add(new HttpUtil.Param("nickname",nickname));
        params.add(new HttpUtil.Param("token",userEntry.getToken()));
        HttpUtil.post(api.IPADDRESS + api.changeuserinfo, new HttpUtil.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                resultCode= JsonUtil.deserialize(response,ResultCode.class);
                if (resultCode.getCode()==0){
                    userEntry.setNickname(nickname);
                    userEntry.setPhone(phone);
                    DB.getInstance(App.instance).Login(userEntry);
                    new ToastUtil().shortDuration("修改成功").show();

                }else if (resultCode.getCode()==1){
                    new ToastUtil().shortDuration("电话已存在").show();
                }
                else {
                    new ToastUtil().shortDuration("服务器错误").show();

                }
                arrayList.get(2).setEnable(false);
                arrayList.get(3).setEnable(false);
                toolbar.getMenu().getItem(0).setVisible(false);
                toolbar.getMenu().getItem(1).setVisible(true);
                toolbar.getMenu().getItem(2).setVisible(true);
                arrayList.clear();
                arrayList.add(new UserInfoItem("ID",String.valueOf(userEntry.getUserid())));
                arrayList.add(new UserInfoItem("邮箱",userEntry.getEmail()));
                arrayList.add(new UserInfoItem("电话",userEntry.getPhone()));
                arrayList.add(new UserInfoItem("昵称",userEntry.getNickname()));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Exception e) {
                new ToastUtil().shortDuration("网络错误").show();
                adapter.notifyDataSetChanged();
            }
        },params);
    }
}
