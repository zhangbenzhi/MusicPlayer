package com.rdc.musicplayer.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.rdc.musicplayer.musicplayer.base.BaseActivity;
import com.rdc.musicplayer.musicplayer.base.BaseFragment;
import com.rdc.musicplayer.musicplayer.fragment.LocalMusicFragment;
import com.rdc.musicplayer.musicplayer.ui.SearchMusicActivity;

import butterknife.BindView;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private FragmentManager mFragmentManager;
    private BaseFragment mCurrentFragment;
    private LocalMusicFragment mLocalMusicFragment;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        initPermission();
    }

    private void initPermission() {
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                initFragment();
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {

            }
        }, permissions);
    }

    @Override
    protected void initView() {

    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mLocalMusicFragment = LocalMusicFragment.newInstance(getResources().getString(R.string.string_local_music));
        fragmentTransaction.add(R.id.fl_content, mLocalMusicFragment);
        fragmentTransaction.commit();
        mCurrentFragment = mLocalMusicFragment;
    }

    @Override
    protected void initListener() {
        mToolbar.setTitle(getResources().getString(R.string.string_local_music));
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);
        mNavView.setItemIconTintList(null);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mCurrentFragment != null) {
            fragmentTransaction.hide(mCurrentFragment);
        }
        switch (item.getItemId()) {
            case R.id.nav_local_music:
                if (mLocalMusicFragment == null) {
                    mLocalMusicFragment = LocalMusicFragment.newInstance(getResources().getString(R.string.string_local_music));
                    fragmentTransaction.add(R.id.fl_content, mLocalMusicFragment);
                } else {
                    fragmentTransaction.show(mLocalMusicFragment);
                }
                mCurrentFragment = mLocalMusicFragment;
                mToolbar.setTitle(getResources().getString(R.string.string_local_music));
                break;
            case R.id.nav_search_music:
                //搜索
                startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.commit();
        return true;
    }

}
