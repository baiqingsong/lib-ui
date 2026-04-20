package com.dawn.lib_ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * 带 TabLayout + ViewPager2 的 Activity 基类。
 * <pre>
 *   public class HomeActivity extends BaseTabActivity {
 *       &#64;Override protected int getLayoutId() { return R.layout.activity_home; }
 *       &#64;Override protected int getTabLayoutId() { return R.id.tab_layout; }
 *       &#64;Override protected int getViewPagerId() { return R.id.view_pager; }
 *       &#64;Override protected void initTabs() {
 *           addTab("首页", new HomeFragment());
 *           addTab("我的", new MineFragment());
 *       }
 *   }
 * </pre>
 */
public abstract class BaseTabActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    protected abstract int getTabLayoutId();
    protected abstract int getViewPagerId();
    protected abstract void initTabs();

    @Override
    protected void initView() {
        tabLayout = findViewById(getTabLayoutId());
        viewPager = findViewById(getViewPagerId());
        initTabs();
        setupViewPager();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void addListener() {
    }

    protected void addTab(@NonNull String title, @NonNull Fragment fragment) {
        titles.add(title);
        fragments.add(fragment);
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(titles.get(position))
        ).attach();
    }

    /**
     * 设置离屏页面缓存数量。
     */
    protected void setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
    }

    /**
     * 切换到指定页签。
     */
    protected void switchToTab(int index) {
        if (viewPager != null && index >= 0 && index < fragments.size()) {
            viewPager.setCurrentItem(index, true);
        }
    }

    @Nullable
    protected TabLayout getTabLayout() {
        return tabLayout;
    }

    @Nullable
    protected ViewPager2 getViewPager() {
        return viewPager;
    }
}
