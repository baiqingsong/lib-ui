package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.dawn.lib_ui.R;

/**
 * 带清除按钮的 EditText — 输入内容时右侧自动显示清除图标，点击即清空。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code cet_clearIcon} — 自定义清除图标（默认系统 ic_menu_close_clear_cancel）</li>
 * </ul>
 */
public class ClearEditText extends AppCompatEditText {

    private Drawable clearDrawable;

    public ClearEditText(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ClearEditText(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearEditText(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
            clearDrawable = a.getDrawable(R.styleable.ClearEditText_cet_clearIcon);
            a.recycle();
        }
        if (clearDrawable == null) {
            clearDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_close_clear_cancel);
        }
        if (clearDrawable != null) {
            clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
        }
        setClearIconVisible(false);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setClearIconVisible(s.length() > 0 && hasFocus());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setOnFocusChangeListener((v, hasFocus) -> {
            Editable text = getText();
            setClearIconVisible(hasFocus && text != null && text.length() > 0);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && clearDrawable != null) {
            Drawable[] drawables = getCompoundDrawables();
            if (drawables[2] != null) {
                int x = (int) event.getX();
                int iconStart = getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth();
                if (x >= iconStart) {
                    setText("");
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void setClearIconVisible(boolean visible) {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1],
                visible ? clearDrawable : null, drawables[3]);
    }
}
