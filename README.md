# lib-ui

Android UI 基础库 — 提供 Activity / Fragment / Application 基类、自定义控件、通用工具类，开箱即用。

## 依赖

```gradle
// 根 build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

// 模块 build.gradle
dependencies {
    implementation 'com.github.baiqingsong:lib-ui:1.0.0'
}
```

## 模块总览

### base — 基类

| 类 | 说明 |
|---|---|
| BaseActivity | Activity 基类：生命周期管理、沉浸式、权限、页面跳转 |
| BaseFragment | Fragment 基类：懒加载、视图缓存 |
| BaseApplication | Application 基类：自动初始化 CrashHandler 和 ActivityStackManager |
| BaseDialogFragment | DialogFragment 基类：生命周期安全的对话框 |
| BaseTabActivity | TabLayout + ViewPager2 的 Activity 基类 |
| BaseListActivity | 下拉刷新 + 加载更多的列表 Activity 基类 |
| BaseListFragment | 下拉刷新 + 加载更多的列表 Fragment 基类 |
| ActivityStackManager | Activity 栈管理器（自动注册生命周期回调） |

### view — 自定义控件

| 类 | 说明 |
|---|---|
| DrawableView | 纯色/渐变背景 View，支持圆角和边框 |
| SuperDrawableTextView | 带渐变背景的 TextView，免 drawable XML |
| CustomSeekBar | 自定义绘制 SeekBar：轨道、滑块、刻度、渐变 |
| RoundImageView | 圆角/圆形 ImageView，支持四角独立设置 |
| ClearEditText | 带清除按钮的 EditText |
| MarqueeTextView | 始终滚动的跑马灯 TextView |
| FlowLayout | 流式标签布局（自动换行） |
| ExpandableTextView | 可展开/收起的 TextView |
| TitleBar | 通用标题栏（返回 + 标题 + 右侧按钮） |
| BadgeView | 数字角标/小红点 |
| StatusBarView | 状态栏高度占位 View |
| DividerView | 分割线（实线/虚线、水平/垂直） |
| StateView | 加载中/空数据/错误三态 View |
| NoScrollGridView | 不可滚动的 GridView（嵌套场景） |
| NoScrollListView | 不可滚动的 ListView（嵌套场景） |
| MaxHeightRecyclerView | 限制最大高度的 RecyclerView |

### adapter — 适配器

| 类 | 说明 |
|---|---|
| BaseRecyclerAdapter | 通用 RecyclerView Adapter，内置 ViewHolder 缓存 |
| MultiTypeAdapter | 多 ViewType RecyclerView Adapter |
| SpacingItemDecoration | RecyclerView 间距装饰（Linear/Grid） |
| ColorDividerDecoration | RecyclerView 线条分割线 |

### dialog — 对话框

| 类 | 说明 |
|---|---|
| BaseDialog | 通用 Dialog 基类：宽度比例、位置、背景暗度 |

### util — 工具类

| 类 | 说明 |
|---|---|
| ScreenUtil | 屏幕宽高、状态栏高度、DPI、横竖屏 |
| DensityUtil | dp↔px、sp↔px 转换 |
| KeyboardUtil | 软键盘显示/隐藏/切换 |
| StatusBarUtil | 状态栏颜色、透明、亮暗模式、沉浸式 |
| ToastUtil | 线程安全的 Toast |
| SPUtil | SharedPreferences 快捷操作 |
| NetworkUtil | 网络连接状态检测 |
| ResourceUtil | 颜色/Drawable/尺寸资源获取 |
| ColorUtil | 颜色混合、亮度调整、暗色判断 |
| AnimUtil | 淡入淡出、滑入滑出、缩放、抖动动画 |
| ViewUtil | View 截图、坐标、测量、遍历 |
| ClickUtil | 全局快速点击防抖 |
| ClickFactory | 按 View 粒度的点击防抖 |
| ProcessUtil | 主进程判断、安装 APK、版本号 |
| CrashHandler | 全局崩溃捕获，文件日志，自定义回调 |

## 使用示例

### BaseActivity

```java
public class HomeActivity extends BaseActivity {
    @Override protected int getLayoutId() { return R.layout.activity_home; }
    @Override protected void initData() { }
    @Override protected void initView() { setTranslucentStatusBar(); }
    @Override protected void addListener() { }
}
```

### BaseFragment

```java
public class HomeFragment extends BaseFragment {
    @Override protected int getLayoutId() { return R.layout.fragment_home; }
    @Override protected void initView(View view) { }
    @Override protected void initData() { }
    @Override protected void addListener() { }
    @Override protected void onLazyLoad() { /* 首次可见时加载 */ }
}
```

### BaseTabActivity

```java
public class HomeActivity extends BaseTabActivity {
    @Override protected int getLayoutId() { return R.layout.activity_home; }
    @Override protected int getTabLayoutId() { return R.id.tab_layout; }
    @Override protected int getViewPagerId() { return R.id.view_pager; }
    @Override protected void initTabs() {
        addTab("首页", new HomeFragment());
        addTab("我的", new MineFragment());
    }
}
```

### BaseListActivity

```java
public class ListActivity extends BaseListActivity {
    @Override protected int getLayoutId() { return R.layout.activity_list; }
    @Override protected int getRecyclerViewId() { return R.id.recycler_view; }
    @Override protected int getSwipeRefreshId() { return R.id.swipe_refresh; }
    @Override protected void onRefresh() { loadPage(1); }
    @Override protected void onLoadMore() { loadPage(nextPage); }
}
```

### BaseDialogFragment

```java
public class MyDialog extends BaseDialogFragment {
    @Override protected int getLayoutId() { return R.layout.dialog_my; }
    @Override protected void initView(View view) {
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> dismiss());
    }
}
new MyDialog().setWidthRatio(0.85f).show(getSupportFragmentManager());
```

### 自定义控件（XML）

```xml
<!-- 圆角渐变背景 -->
<com.dawn.lib_ui.view.DrawableView
    app:dv_solidColor="#4CAF50"
    app:dv_cornerRadius="12dp"
    app:dv_borderWidth="2dp"
    app:dv_borderColor="#388E3C" />

<!-- 渐变按钮 -->
<com.dawn.lib_ui.view.SuperDrawableTextView
    android:text="渐变按钮"
    app:stv_backgroundSolid="#FF5722"
    app:stv_cornerRadius="8dp" />

<!-- 圆角图片 -->
<com.dawn.lib_ui.view.RoundImageView
    app:riv_cornerRadius="16dp"
    app:riv_isCircle="true"
    app:riv_borderWidth="2dp"
    app:riv_borderColor="#2196F3" />

<!-- 带清除按钮的输入框 -->
<com.dawn.lib_ui.view.ClearEditText
    android:hint="输入关键词" />

<!-- 跑马灯文本 -->
<com.dawn.lib_ui.view.MarqueeTextView
    android:text="这是一段很长的跑马灯文本..." />

<!-- 流式标签布局 -->
<com.dawn.lib_ui.view.FlowLayout
    app:fl_horizontalSpacing="8dp"
    app:fl_verticalSpacing="8dp" />

<!-- 通用标题栏 -->
<com.dawn.lib_ui.view.TitleBar
    app:tb_title="页面标题"
    app:tb_showBack="true"
    app:tb_rightText="保存" />

<!-- 分割线 -->
<com.dawn.lib_ui.view.DividerView
    app:div_color="#E0E0E0"
    app:div_thickness="1dp"
    app:div_dashWidth="4dp"
    app:div_dashGap="2dp" />

<!-- 自定义 SeekBar -->
<com.dawn.lib_ui.view.CustomSeekBar
    app:csb_progressColor="#FF4081"
    app:csb_thumbSize="20dp"
    app:csb_thumbColor="#FFFFFF"
    app:csb_thumbBorderWidth="2dp"
    app:csb_thumbBorderColor="#FF4081" />
```

### 工具类

```java
// Toast（任意线程安全调用）
ToastUtil.show("提示");

// SharedPreferences
SPUtil.init(context);
SPUtil.put("key", "value");
String v = SPUtil.getString("key", "");

// 状态栏
StatusBarUtil.setTransparentStatusBar(activity);
StatusBarUtil.setLightMode(activity);

// 网络
boolean connected = NetworkUtil.isNetworkAvailable(context);
NetworkUtil.NetworkType type = NetworkUtil.getNetworkType(context);

// 动画
AnimUtil.fadeIn(view);
AnimUtil.shake(view);  // 抖动提示

// 颜色
int dark = ColorUtil.darken(color, 0.8f);
boolean isDark = ColorUtil.isDarkColor(color);

// RecyclerView 分割线
recyclerView.addItemDecoration(new ColorDividerDecoration(context, Color.LTGRAY, 1, 16, 16));
recyclerView.addItemDecoration(new SpacingItemDecoration(16));

// 多类型 Adapter
MultiTypeAdapter adapter = new MultiTypeAdapter();
adapter.register(Header.class, R.layout.item_header, (h, item, pos) -> { });
adapter.register(Content.class, R.layout.item_content, (h, item, pos) -> { });

// StateView
stateView.showLoading();
stateView.showEmpty("暂无数据");
stateView.showError("加载失败", v -> reload());
```

## AndroidX 依赖

- appcompat 1.6.1
- recyclerview 1.3.1
- constraintlayout 2.1.4
- viewpager2 1.0.0
- swiperefreshlayout 1.1.0
- cardview 1.0.0
- material 1.9.0

## 最低版本

- minSdk 28
- compileSdk 34
- Java 8
