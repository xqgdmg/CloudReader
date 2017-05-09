package com.example.jingbin.cloudreader;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jingbin.cloudreader.app.ConstantsImageUrl;
import com.example.jingbin.cloudreader.databinding.ActivityMainBinding;
import com.example.jingbin.cloudreader.databinding.NavHeaderMainBinding;
import com.example.jingbin.cloudreader.http.rx.RxBus;
import com.example.jingbin.cloudreader.http.rx.RxBusBaseMessage;
import com.example.jingbin.cloudreader.http.rx.RxCodeConstants;
import com.example.jingbin.cloudreader.ui.book.BookFragment;
import com.example.jingbin.cloudreader.ui.gank.GankFragment;
import com.example.jingbin.cloudreader.ui.menu.NavAboutActivity;
import com.example.jingbin.cloudreader.ui.menu.NavDeedBackActivity;
import com.example.jingbin.cloudreader.ui.menu.NavDownloadActivity;
import com.example.jingbin.cloudreader.ui.menu.NavHomePageActivity;
import com.example.jingbin.cloudreader.ui.one.OneFragment;
import com.example.jingbin.cloudreader.utils.CommonUtils;
import com.example.jingbin.cloudreader.utils.ImgLoadUtil;
import com.example.jingbin.cloudreader.utils.PerfectClickListener;
import com.example.jingbin.cloudreader.utils.SPUtils;
import com.example.jingbin.cloudreader.view.MyFragmentPagerAdapter;
import com.example.jingbin.cloudreader.view.statusbar.StatusBarUtil;
import com.example.jingbin.cloudreader.view.webview.WebViewActivity;

import java.util.ArrayList;

import rx.functions.Action1;


/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private FrameLayout llTitleMenu;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private NavigationView navView;
    private DrawerLayout drawerLayout;
    private ViewPager vpContent;

    // 一定需要对应的bean
    private ActivityMainBinding mBinding;
    private ImageView ivMainFragment01;
    private ImageView ivMainFragment02;
    private ImageView ivMainFragment03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initStatusView();
        initId();
        initRxBus();

        StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, drawerLayout,
                CommonUtils.getColor(R.color.colorTheme));

        initContentFragment();
        initDrawerLayout();
        initListener();
    }

    private void initStatusView() {
        ViewGroup.LayoutParams layoutParams = mBinding.include.viewStatus.getLayoutParams();
        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
        mBinding.include.viewStatus.setLayoutParams(layoutParams);
    }

    private void initId() {
        drawerLayout = mBinding.drawerLayout;
        navView = mBinding.navView;
        fab = mBinding.include.fab;
        toolbar = mBinding.include.toolbar;
        llTitleMenu = mBinding.include.llTitleMenu;
        vpContent = mBinding.include.vpContent;
        fab.setVisibility(View.GONE);

        ivMainFragment01 = mBinding.include.ivMainFragment01;
        ivMainFragment02 = mBinding.include.ivMainFragment02;
        ivMainFragment03 = mBinding.include.ivMainFragment03;
    }

    private void initListener() {
        llTitleMenu.setOnClickListener(this);
        mBinding.include.ivMainFragment01.setOnClickListener(this);
        mBinding.include.ivMainFragment03.setOnClickListener(this);
        mBinding.include.ivMainFragment02.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    NavHeaderMainBinding bind;

    /**
     * inflateHeaderView 进来的布局要宽一些
     */
    private void initDrawerLayout() {
        navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navView.getHeaderView(0);
        bind = DataBindingUtil.bind(headerView);
        bind.setListener(this);
        bind.dayNightSwitch.setChecked(SPUtils.getNightMode());

        ImgLoadUtil.displayCircle(bind.ivAvatar, ConstantsImageUrl.IC_AVATAR);
        bind.llNavExit.setOnClickListener(this);
        bind.ivAvatar.setOnClickListener(this);

        bind.llNavHomepage.setOnClickListener(listener);
        bind.llNavScanDownload.setOnClickListener(listener);
        bind.llNavDeedback.setOnClickListener(listener);
        bind.llNavAbout.setOnClickListener(listener);
        bind.llNavLogin.setOnClickListener(listener);
    }

    private void initContentFragment() {
        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new GankFragment());
        mFragmentList.add(new OneFragment());
        mFragmentList.add(new BookFragment());
        // 注意使用的是：getSupportFragmentManager
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        vpContent.setAdapter(adapter);

        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);
        mBinding.include.ivMainFragment01.setSelected(true);
        vpContent.setCurrentItem(0);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * 避免在1秒内出发多次点击
     */
    private PerfectClickListener listener = new PerfectClickListener() {
        @Override
        protected void onNoDoubleClick(final View v) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            mBinding.drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
                        case R.id.ll_nav_homepage:// 主页
                            NavHomePageActivity.startHome(MainActivity.this);
                            break;
                        case R.id.ll_nav_scan_download://扫码下载
                            NavDownloadActivity.start(MainActivity.this);
                            break;
                        case R.id.ll_nav_deedback:// 问题反馈
                            NavDeedBackActivity.start(MainActivity.this);
                            break;
                        case R.id.ll_nav_about:// 关于云阅
                            NavAboutActivity.start(MainActivity.this);
                            break;
                        case R.id.ll_nav_login:// 登录GitHub账号
                            WebViewActivity.loadUrl(v.getContext(), "https://github.com/login", "登录GitHub账号");
                            break;
                    }
                }
            }, 260);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_title_menu:// 开启菜单
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_main_fragment01:// 干货栏
                if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                    ivMainFragment01.setSelected(true);
                    ivMainFragment02.setSelected(false);
                    ivMainFragment03.setSelected(false);
                    vpContent.setCurrentItem(0);
                }
                break;
            case R.id.iv_main_fragment02:// 电影栏
                if (vpContent.getCurrentItem() != 1) {
                    ivMainFragment02.setSelected(true);
                    ivMainFragment01.setSelected(false);
                    ivMainFragment03.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;
            case R.id.iv_main_fragment03:// 书籍栏
                if (vpContent.getCurrentItem() != 2) {
                    ivMainFragment03.setSelected(true);
                    ivMainFragment02.setSelected(false);
                    ivMainFragment01.setSelected(false);
                    vpContent.setCurrentItem(2);
                }
                break;
            case R.id.iv_avatar: // 头像进入GitHub
                WebViewActivity.loadUrl(v.getContext(),CommonUtils.getString(R.string.string_url_cloudreader),"CloudReader");
                break;
            case R.id.ll_nav_exit:// 退出应用
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 夜间模式待完善
     */
    public boolean getNightMode() {
        return SPUtils.getNightMode();
    }

    public void onNightModeClick(View view) {
        if (!SPUtils.getNightMode()) {
//            SkinCompatManager.getInstance().loadSkin(Constants.NIGHT_SKIN);
        } else {
            // 恢复应用默认皮肤
//            SkinCompatManager.getInstance().restoreDefaultTheme();
        }
        SPUtils.setNightMode(!SPUtils.getNightMode());
        bind.dayNightSwitch.setChecked(SPUtils.getNightMode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
//                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                ivMainFragment01.setSelected(true);
                ivMainFragment02.setSelected(false);
                ivMainFragment03.setSelected(false);
                break;
            case 1:
                ivMainFragment02.setSelected(true);
                ivMainFragment01.setSelected(false);
                ivMainFragment03.setSelected(false);
                break;
            case 2:
                ivMainFragment03.setSelected(true);
                ivMainFragment02.setSelected(false);
                ivMainFragment01.setSelected(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 每日推荐点击"新电影热映榜"跳转
     */
    private void initRxBus() {
        RxBus.getDefault().toObservable(RxCodeConstants.JUMP_TYPE_TO_ONE, RxBusBaseMessage.class)
                .subscribe(new Action1<RxBusBaseMessage>() {
                    @Override
                    public void call(RxBusBaseMessage integer) {
                        mBinding.include.vpContent.setCurrentItem(1);
                    }
                });
    }
}
