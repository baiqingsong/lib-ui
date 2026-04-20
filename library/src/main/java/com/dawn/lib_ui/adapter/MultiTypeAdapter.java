package com.dawn.lib_ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 多类型 RecyclerView Adapter — 支持注册多种 ItemType。
 * <pre>
 *   MultiTypeAdapter adapter = new MultiTypeAdapter();
 *   adapter.register(HeaderItem.class, R.layout.item_header, (holder, item, pos) -> {
 *       holder.setText(R.id.tv_title, item.getTitle());
 *   });
 *   adapter.register(ContentItem.class, R.layout.item_content, (holder, item, pos) -> {
 *       holder.setText(R.id.tv_body, item.getBody());
 *   });
 *   adapter.setData(mixedList);
 * </pre>
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {

    private final List<Object> dataList = new ArrayList<>();
    private final SparseArray<ItemBinder<?>> binders = new SparseArray<>();
    private final List<Class<?>> typeClasses = new ArrayList<>();

    /**
     * 绑定回调。
     */
    public interface ItemBinder<T> {
        void onBind(@NonNull BaseRecyclerAdapter.ViewHolder holder, @NonNull T item, int position);
    }

    private static class TypeInfo {
        final int layoutId;
        final ItemBinder<?> binder;

        TypeInfo(int layoutId, ItemBinder<?> binder) {
            this.layoutId = layoutId;
            this.binder = binder;
        }
    }

    private final SparseArray<TypeInfo> typeInfos = new SparseArray<>();

    /**
     * 注册一种数据类型。
     */
    @SuppressWarnings("unchecked")
    public <T> MultiTypeAdapter register(@NonNull Class<T> clazz, @LayoutRes int layoutId,
                                         @NonNull ItemBinder<T> binder) {
        int type;
        int index = typeClasses.indexOf(clazz);
        if (index >= 0) {
            type = index;
        } else {
            typeClasses.add(clazz);
            type = typeClasses.size() - 1;
        }
        typeInfos.put(type, new TypeInfo(layoutId, binder));
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = dataList.get(position);
        for (int i = 0; i < typeClasses.size(); i++) {
            if (typeClasses.get(i).isInstance(item)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unregistered type: " + item.getClass().getName());
    }

    @NonNull
    @Override
    public BaseRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypeInfo info = typeInfos.get(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(info.layoutId, parent, false);
        return new BaseRecyclerAdapter.ViewHolder(view);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onBindViewHolder(@NonNull BaseRecyclerAdapter.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        TypeInfo info = typeInfos.get(type);
        if (info != null) {
            ((ItemBinder) info.binder).onBind(holder, dataList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(@NonNull List<?> data) {
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(@NonNull List<?> data) {
        int start = dataList.size();
        dataList.addAll(data);
        notifyItemRangeInserted(start, data.size());
    }

    public void addItem(@NonNull Object item) {
        dataList.add(item);
        notifyItemInserted(dataList.size() - 1);
    }

    public void clear() {
        int size = dataList.size();
        dataList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    public List<Object> getData() {
        return dataList;
    }
}
