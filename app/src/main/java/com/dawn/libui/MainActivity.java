package com.dawn.libui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dawn.lib_ui.adapter.BaseRecyclerAdapter;
import com.dawn.lib_ui.adapter.SpacingItemDecoration;
import com.dawn.lib_ui.base.BaseActivity;
import com.dawn.lib_ui.util.ClickFactory;
import com.dawn.lib_ui.util.DensityUtil;
import com.dawn.lib_ui.util.ScreenUtil;
import com.dawn.lib_ui.view.CustomSeekBar;
import com.dawn.lib_ui.view.DrawableView;
import com.dawn.lib_ui.view.RoundImageView;
import com.dawn.lib_ui.view.SuperDrawableTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        // no-op
    }

    @Override
    protected void initView() {
        // DrawableView 演示
        DrawableView drawableView = findViewById(R.id.drawable_view);
        drawableView.setSolidColor(Color.parseColor("#4CAF50"));
        drawableView.setCornerRadius(DensityUtil.dp2px(this, 12));

        // SuperDrawableTextView 演示
        SuperDrawableTextView superTv = findViewById(R.id.super_tv);
        superTv.setGradientBackground(
                new int[]{Color.parseColor("#FF5722"), Color.parseColor("#FFC107")},
                0, 0);
        superTv.setCornerRadius(DensityUtil.dp2px(this, 8));

        // RoundImageView 演示
        RoundImageView roundIv = findViewById(R.id.round_iv);
        roundIv.setCornerRadius(DensityUtil.dp2px(this, 16));
        roundIv.setImageResource(android.R.drawable.ic_menu_gallery);

        // CustomSeekBar 演示
        CustomSeekBar seekBar = findViewById(R.id.custom_seek_bar);
        seekBar.setMax(100);
        seekBar.setProgress(50);

        // RecyclerView 演示
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(DensityUtil.dp2px(this, 8)));

        List<String> items = new ArrayList<>(Arrays.asList(
                "BaseActivity", "BaseFragment", "BaseApplication",
                "DrawableView", "SuperDrawableTextView", "CustomSeekBar",
                "RoundImageView", "BaseDialog", "BaseRecyclerAdapter",
                "ScreenUtil", "DensityUtil", "KeyboardUtil",
                "ClickUtil", "ClickFactory", "CrashHandler"
        ));

        BaseRecyclerAdapter<String> adapter = new BaseRecyclerAdapter<String>(
                this, android.R.layout.simple_list_item_1, items) {
            @Override
            protected void onBind(ViewHolder holder, String item, int position) {
                holder.setText(android.R.id.text1, item);
            }
        };

        adapter.setOnItemClickListener((view, item, position) ->
                Toast.makeText(this, "点击: " + item, Toast.LENGTH_SHORT).show());
        recyclerView.setAdapter(adapter);

        // 屏幕信息
        int sw = ScreenUtil.getScreenWidth(this);
        int sh = ScreenUtil.getScreenHeight(this);
        Toast.makeText(this, "屏幕: " + sw + " x " + sh, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void addListener() {
        // no-op
    }
}
