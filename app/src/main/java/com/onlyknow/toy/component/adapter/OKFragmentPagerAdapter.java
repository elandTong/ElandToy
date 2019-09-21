package com.onlyknow.toy.component.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.onlyknow.toy.component.OKBaseFragment;
import com.onlyknow.toy.utils.image.OKBannerLoad;

import java.util.List;

public class OKFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<OKBaseFragment> fragmentList;
    private List<CharSequence> titleList;

    public OKFragmentPagerAdapter(FragmentManager fragmentManager, List<OKBaseFragment> fragments, List<CharSequence> titleList) {
        super(fragmentManager);

        this.fragmentList = fragments;

        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
