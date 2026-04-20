package com.dawn.lib_ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 带下拉刷新和加载更多的列表 Fragment 基类。
 */
public abstract class BaseListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoadingMore = false;
    private boolean loadMoreEnabled = true;

    protected abstract int getRecyclerViewId();
    protected abstract int getSwipeRefreshId();
    protected abstract void onRefresh();

    protected void onLoadMore() {
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(getRecyclerViewId());
        swipeRefreshLayout = view.findViewById(getSwipeRefreshId());

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    protected void finishRefresh() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

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
