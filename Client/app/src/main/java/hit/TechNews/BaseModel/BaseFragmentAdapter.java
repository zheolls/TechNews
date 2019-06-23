package hit.TechNews.BaseModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class BaseFragmentAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private BaseFragment [] fragments;
    private String[] mTitles;

    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    public BaseFragmentAdapter(FragmentManager fragmentManager, BaseFragment[] baseFragments){
        super(fragmentManager);
        this.fm=fragmentManager;
        this.fragments=baseFragments;
    }
    public BaseFragmentAdapter(FragmentManager fm, BaseFragment[] fragmentList, String[] mTitles) {
        super(fm);
        this.fm = fm;
        this.fragments = fragmentList;
        this.mTitles = mTitles;
    }
    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        if(fragments == null) return 0;
        return fragments.length;    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles == null ? "" : mTitles[position];

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment=fragments[position];
        fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
//        super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
//        return super.instantiateItem(container, position);
    }

    public void setmTitles(String[] mTitles) {
        this.mTitles = mTitles;
    }

    public String[] getmTitles() {
        return mTitles;
    }
}
