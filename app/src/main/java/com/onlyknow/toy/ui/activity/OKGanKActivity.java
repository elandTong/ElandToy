package com.onlyknow.toy.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.ArgbEvaluator;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.onlyknow.toy.R;
import com.onlyknow.toy.component.OKBaseActivity;
import com.onlyknow.toy.component.OKBaseFragment;
import com.onlyknow.toy.component.adapter.OKFragmentPagerAdapter;
import com.onlyknow.toy.ui.fragement.gank.OKGanKAndroidFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKAppFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKFrontEndFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKIosFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKRecommendFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKResFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKVideoFragment;
import com.onlyknow.toy.ui.fragement.gank.OKGanKWelfareFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * GanK.Io
 * <p>
 * Created by Administrator on 2018/2/6.
 */

public class OKGanKActivity extends OKBaseActivity {
    @Bind(R.id.ok_activity_gank_toolbar)
    Toolbar toolbar;

    @Bind(R.id.ok_activity_gank_tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.ok_activity_gank_appBarLayout)
    AppBarLayout appBarLayout;

    @Bind(R.id.ok_activity_gank_top_image)
    FloatingActionButton toppingButton;

    @Bind(R.id.ok_activity_gank_viewPage)
    ViewPager viewPage;

    @Bind(R.id.ok_activity_gank_drawerLayout)
    DrawerLayout drawerLayout;

    @Bind(R.id.ok_activity_gank_NavigationView)
    NavigationView navigationView;

    public final static String INTENT_KEY_GAN_KIO = "INTENT_KEY_GAN_KIO";
    public final static int GAN_KIO_TYPE_FL = 1;
    public final static int GAN_KIO_TYPE_VIDEO = 2;
    public final static int GAN_KIO_TYPE_RES = 3;
    public final static int GAN_KIO_TYPE_ANDROID = 4;
    public final static int GAN_KIO_TYPE_IOS = 5;
    public final static int GAN_KIO_TYPE_H5 = 6;

    private final List<OKBaseFragment> fragments = new ArrayList<>();

    private final List<CharSequence> tabNames = new ArrayList<>();

    private final int[] colors = {R.color.md_light_green_500,
            R.color.md_pink_500,
            R.color.md_purple_500,
            R.color.md_deep_purple_500,
            R.color.md_teal_500,
            R.color.md_deep_orange_500,
            R.color.md_green_500,
            R.color.md_brown_500};

    private OKFragmentPagerAdapter fragmentPagerAdapter;

    private DrawerToggle drawerToggle;

    private OKGanKWelfareFragment welfareFragment = new OKGanKWelfareFragment(colors[0]);
    private OKGanKVideoFragment videoFragment = new OKGanKVideoFragment(colors[1]);
    private OKGanKResFragment resFragment = new OKGanKResFragment(colors[2]);
    private OKGanKRecommendFragment recommendFragment = new OKGanKRecommendFragment(colors[3]);
    private OKGanKAppFragment appFragment = new OKGanKAppFragment(colors[4]);
    private OKGanKAndroidFragment androidFragment = new OKGanKAndroidFragment(colors[5]);
    private OKGanKIosFragment iosFragment = new OKGanKIosFragment(colors[6]);
    private OKGanKFrontEndFragment h5Fragment = new OKGanKFrontEndFragment(colors[7]);

    private int pageIndex = 0;

    private void init() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Bundle bundle = new Bundle();

                switch (id) {
                    case R.id.ok_menu_drawer_FuLi:
                        viewPage.setCurrentItem(0);
                        tabLayout.setScrollPosition(0, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_Video:
                        viewPage.setCurrentItem(1);
                        tabLayout.setScrollPosition(1, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_ExtensionRes:
                        viewPage.setCurrentItem(2);
                        tabLayout.setScrollPosition(2, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_recommend:
                        viewPage.setCurrentItem(3);
                        tabLayout.setScrollPosition(3, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_app:
                        viewPage.setCurrentItem(4);
                        tabLayout.setScrollPosition(4, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_Android:
                        viewPage.setCurrentItem(5);
                        tabLayout.setScrollPosition(5, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_IOS:
                        viewPage.setCurrentItem(6);
                        tabLayout.setScrollPosition(6, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_QianDuan:
                        viewPage.setCurrentItem(7);
                        tabLayout.setScrollPosition(7, 0f, true);
                        break;

                    case R.id.ok_menu_drawer_fav:
                        bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);
                        startUserActivity(bundle, OKGanKFavActivity.class);
                        break;

                    case R.id.ok_menu_drawer_qrcode:
                        startUserActivity(null, OKQRCodeScanningActivity.class);
                        break;

                    case R.id.ok_menu_drawer_submit:
                        bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);
                        startUserActivity(bundle, OKGanKSubmitActivity.class);
                        break;

                    case R.id.ok_menu_drawer_Setting:
                        bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);
                        startUserActivity(bundle, OKSettingsActivity.class);
                        break;

                    default:
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        drawerToggle = new DrawerToggle(drawerLayout);
        drawerToggle.syncState();

        drawerLayout.addDrawerListener(drawerToggle);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);// 设置TabLayout的模式

        // 添加 tab 标签
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_welfare)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_video)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_res)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_recommend)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_app)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_android)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_ios)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.gank_h5)));

        // 添加 tab 内容组件
        fragments.clear();
        fragments.add(welfareFragment);
        fragments.add(videoFragment);
        fragments.add(resFragment);
        fragments.add(recommendFragment);
        fragments.add(appFragment);
        fragments.add(androidFragment);
        fragments.add(iosFragment);
        fragments.add(h5Fragment);

        // 添加 tab 名称
        tabNames.clear();
        tabNames.add(getText(R.string.gank_welfare));
        tabNames.add(getText(R.string.gank_video));
        tabNames.add(getText(R.string.gank_res));
        tabNames.add(getText(R.string.gank_recommend));
        tabNames.add(getText(R.string.gank_app));
        tabNames.add(getText(R.string.gank_android));
        tabNames.add(getText(R.string.gank_ios));
        tabNames.add(getText(R.string.gank_h5));

        // 添加 切换主题颜色
        fragmentPagerAdapter = new OKFragmentPagerAdapter(this.getSupportFragmentManager(), fragments, tabNames);

        viewPage.setAdapter(fragmentPagerAdapter);

        viewPage.setOffscreenPageLimit(6);

        tabLayout.setupWithViewPager(viewPage);

        viewPage.setCurrentItem(pageIndex);

        tabLayout.setScrollPosition(pageIndex, 0f, true);

        colorInTheme = getResources().getColor(colors[pageIndex]);

        navigationView.getHeaderView(0).setBackgroundColor(colorInTheme);

        toppingButton.setBackgroundTintList(ColorStateList.valueOf(colorInTheme));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) { // TAB 切换回调
                int colorEnd = getResources().getColor(colors[tab.getPosition()]);

                @SuppressLint("RestrictedApi")
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorInTheme, colorEnd);

                colorAnimation.setDuration(1000); // 动画执行时间

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // 动画监听器

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        int color = (int) animator.getAnimatedValue();

                        toolbar.setBackgroundColor(color); // 修改 toolbar 背景颜色

                        tabLayout.setBackgroundColor(color); // 修改 tab layout 背景颜色

                        navigationView.getHeaderView(0).setBackgroundColor(color); // 设置抽屉菜单 head 背景颜色

                        toppingButton.setBackgroundTintList(ColorStateList.valueOf(color)); // 设置浮动按钮颜色

                        setStatusBar(color); // 设置状态栏颜色

                        colorInTheme = color;
                    }
                });

                colorAnimation.start(); // 执行动画
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        toppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welfareFragment.stickTop();

                videoFragment.stickTop();

                resFragment.stickTop();

                androidFragment.stickTop();

                iosFragment.stickTop();

                h5Fragment.stickTop();

                appFragment.stickTop();

                recommendFragment.stickTop();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ok_activity_gank);

        ButterKnife.bind(this);

        initStatusBar();

        setSupportActionBar(toolbar);

        welfareFragment.setParentActivity(this);
        videoFragment.setParentActivity(this);
        resFragment.setParentActivity(this);
        androidFragment.setParentActivity(this);
        iosFragment.setParentActivity(this);
        h5Fragment.setParentActivity(this);
        appFragment.setParentActivity(this);
        recommendFragment.setParentActivity(this);

        int type = intentBundle.getInt(INTENT_KEY_GAN_KIO, GAN_KIO_TYPE_FL);

        switch (type) {
            case GAN_KIO_TYPE_FL:
                pageIndex = 0;

                break;
            case GAN_KIO_TYPE_VIDEO:
                pageIndex = 1;

                break;
            case GAN_KIO_TYPE_RES:
                pageIndex = 2;

                break;
            case GAN_KIO_TYPE_ANDROID:
                pageIndex = 3;

                break;
            case GAN_KIO_TYPE_IOS:
                pageIndex = 4;

                break;
            case GAN_KIO_TYPE_H5:
                pageIndex = 5;

                break;
            default:
                finish();

                break;
        }

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (toolbar != null) {
            toolbar.setTitle(getText(R.string.gank_toolbar));
            toolbar.setLogo(R.drawable.ok_gank_work);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok_menu_gank, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok_menu_gank_about:
                Bundle bundle = new Bundle();

                bundle.putString(OKWebActivity.WEB_LINK, "http://gank.io/");
                bundle.putString(OKWebActivity.WEB_TITLE, getString(R.string.gank_about));
                bundle.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                startUserActivity(bundle, OKWebActivity.class);

                break;
            case R.id.ok_menu_gank_search:
                Bundle bundle_search = new Bundle();
                bundle_search.putInt(OKBaseActivity.ACT_COLOR_THEME, colorInTheme);

                startUserActivity(bundle_search, OKGanKSearchActivity.class);

                break;
        }

        return true;
    }

    private class DrawerToggle extends ActionBarDrawerToggle {
        @SuppressLint("ResourceType")
        public DrawerToggle(DrawerLayout drawerLayout) {
            super(OKGanKActivity.this, drawerLayout, toolbar, R.drawable.ok_toolbar_menu, R.drawable.ok_toolbar_back);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }
    }
}
