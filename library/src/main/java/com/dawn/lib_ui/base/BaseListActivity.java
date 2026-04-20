package com.dawn.lib_ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 带下拉刷新和加载更多的列表 Activity 基类。
 * <p>
 * 子类需要在布局中放置 {@code SwipeRefreshLayout} 包裹 {@code RecyclerView}，
 * 然后实现 {@link #getRecyclerViewId()} 和 {@link #getSwipeRefreshId()}。
 * <pre>
 *   public class ListActivity extends BaseListActivity {
 *       &#64;Override protected int getLayoutId() { return R.layout.activity_list; }
 *       &#64;Override protected int getRecyclerViewId() { return R.id.recycler_view; }
 *       &#64;Override protected int getSwipeRefreshId() { return R.id.swipe_refresh; }
 *       &#64;Override protected void onRefresh() { loadData(true); }
 *       &#64;Override protected void onLoadMore() { loadData(false); }
 *   }
 * </pre>
 */
public abstract class BaseListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoadingMore = false;
    private boolean loadMoreEnabled = true;

    protected abstract int getRecyclerViewId();
    protected abstract int getSwipeRefreshId();

    /**
     * 下拉刷新回调。
     */
    protected abstract void onRefresh();

    /**
     * 上拉加载更多回调（默认空实现）。
     */
    protected void onLoadMore() {
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(getRecyclerViewId());
        swipeRefreshLayout = findViewById(getSwipeRefreshId());

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!loadMoreEnabled || isLoadingMore) return;
                RecyclerView.LayoutManager lm = rv.getLayoutManager();
                if (lm instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) lm;
                    int total = llm.getItemCount();
                    int lastVisible = llm.findLastVisibleItemPosition();
                    if (total > 0 && lastVisible >= total - 2) {
                        isLoadingMore = true;
                        onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void addListener() {
    }

    /**
     * 完成刷新（隐藏刷新动画）。
     */
    protected void finishRefresh() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 完成加载更多。
     */
    protected void finishLoadMore() {
        isLoadingMore = false;
    }

    protected void setLoadMoreEnabled(boolean enabled) {
        this.loadMoreEnabled = enabled;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
