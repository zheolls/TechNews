package hit.TechNews.Mine;

import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import hit.TechNews.BaseModel.BaseActivity;
import hit.TechNews.BaseModel.BaseFragment;
import hit.TechNews.BaseModel.BaseFragmentAdapter;
import hit.TechNews.R;

public class LoginActivity extends BaseActivity {
    BaseFragment baseFragment;
    @BindView(R.id.signtoolbar)
    Toolbar toolbar;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView() {
        baseFragment=SigninFragment.getInstance();
        BaseFragmentAdapter mAdapter=new BaseFragmentAdapter(getSupportFragmentManager());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signlayout,baseFragment)
                .commit();
    }
}
