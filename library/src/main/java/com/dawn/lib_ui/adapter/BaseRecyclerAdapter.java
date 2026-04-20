package com.dawn.lib_ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用 RecyclerView Adapter 基类 — 无需为每个列表写独立 Adapter。
 * <p>
 * 使用示例：
 * <pre>
 *   BaseRecyclerAdapter&lt;String&gt; adapter = new BaseRecyclerAdapter&lt;String&gt;(this, R.layout.item_text, dataList) {
 *       &#64;Override
 *       protected void onBind(ViewHolder holder, String item, int position) {
 *           holder.setText(R.id.tv_name, item);
 *       }
 *   };
 * </pre>
 *
 * @param <T> 数据类型
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final int layoutId;
    private final List<T> dataList;
    private OnItemClickListener<T> itemClickListener;
    private OnItemLongClickListener<T> itemLongClickListener;

    public BaseRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutId, @NonNull List<T> dataList) {
        this.context = context;
        this.layoutId = layoutId;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = dataList.get(position);
        onBind(holder, item, position);

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(v, dataList.get(pos), pos);
                }
            });
        }
        if (itemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    return itemLongClickListener.onItemLongClick(v, dataList.get(pos), pos);
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 绑定数据（子类实现）。
     */
    protected abstract void onBind(@NonNull ViewHolder holder, @NonNull T item, int position);

    // =============== 数据操作 ===============

    public List<T> getData() {
        return dataList;
    }

    public void setData(List<T> newData) {
        dataList.clear();
        if (newData != null) {
            dataList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    public void addData(T item) {
        dataList.add(item);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addData(List<T> items) {
        if (items == null || items.isEmpty()) return;
        int start = dataList.size();
        dataList.addAll(items);
        notifyItemRangeInserted(start, items.size());
    }

    public void removeData(int position) {
        if (position >= 0 && position < dataList.size()) {
            dataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, dataList.size() - position);
        }
    }

    public void clear() {
        int size = dataList.size();
        dataList.clear();
        notifyItemRangeRemoved(0, size);
    }

    // =============== 监听器 ===============

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.itemLongClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View view, T item, int position);
    }

    // =============== ViewHolder ===============

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final SparseArray<View> views = new SparseArray<>();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V getView(@IdRes int id) {
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return (V) view;
        }

        public ViewHolder setText(@IdRes int id, CharSequence text) {
            TextView tv = getView(id);
            tv.setText(text);
            return this;
        }

        public ViewHolder setImageResource(@IdRes int id, int resId) {
            ImageView iv = getView(id);
            iv.setImageResource(resId);
            return this;
        }

        public ViewHolder setVisibility(@IdRes int id, int visibility) {
            getView(id).setVisibility(visibility);
            return this;
        }

        public ViewHolder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }

        public ViewHolder setEnabled(@IdRes int id, boolean enabled) {
            getView(id).setEnabled(enabled);
            return this;
        }
    }
}
