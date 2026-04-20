package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dawn.lib_ui.R;

/**
 * 可展开/收起的 TextView — 超过指定行数时显示 "展开/收起" 按钮。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code etv_maxCollapsedLines} — 折叠时最大行数（默认 3）</li>
 *   <li>{@code etv_expandText} — 展开按钮文本（默认"展开"）</li>
 *   <li>{@code etv_collapseText} — 收起按钮文本（默认"收起"）</li>
 * </ul>
 */
public class ExpandableTextView extends LinearLayout {

    private static final int DEFAULT_MAX_LINES = 3;

    private TextView contentTextView;
    private TextView toggleTextView;
    private int maxCollapsedLines;
    private String expandText;
    private String collapseText;
    private boolean isExpanded = false;
    private boolean needsToggle = false;

    public ExpandableTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);

        maxCollapsedLines = DEFAULT_MAX_LINES;
        expandText = "展开";
        collapseText = "收起";

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
            maxCollapsedLines = a.getInteger(R.styleable.ExpandableTextView_etv_maxCollapsedLines, DEFAULT_MAX_LINES);
            String et = a.getString(R.styleable.ExpandableTextView_etv_expandText);
            if (et != null) expandText = et;
            String ct = a.getString(R.styleable.ExpandableTextView_etv_collapseText);
            if (ct != null) collapseText = ct;
            a.recycle();
        }

        contentTextView = new TextView(context);
        contentTextView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentTextView.setMaxLines(maxCollapsedLines);
        contentTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(contentTextView);

        toggleTextView = new TextView(context);
        toggleTextView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleTextView.setText(expandText);
        toggleTextView.setTextColor(0xFF2196F3);
        toggleTextView.setVisibility(GONE);
        addView(toggleTextView);

        toggleTextView.setOnClickListener(v -> toggle());
    }

    public void setText(CharSequence text) {
        contentTextView.setText(text);
        contentTextView.post(() -> {
            int lineCount = contentTextView.getLineCount();
            needsToggle = lineCount > maxCollapsedLines;
            toggleTextView.setVisibility(needsToggle ? VISIBLE : GONE);
            if (!isExpanded) {
                contentTextView.setMaxLines(maxCollapsedLines);
                toggleTextView.setText(expandText);
            }
        });
    }

    public void toggle() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            contentTextView.setMaxLines(Integer.MAX_VALUE);
            contentTextView.setEllipsize(null);
            toggleTextView.setText(collapseText);
        } else {
            contentTextView.setMaxLines(maxCollapsedLines);
            contentTextView.setEllipsize(TextUtils.TruncateAt.END);
            toggleTextView.setText(expandText);
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    @NonNull
    public TextView getContentTextView() {
        return contentTextView;
    }

    @NonNull
    public TextView getToggleTextView() {
        return toggleTextView;
    }
}
