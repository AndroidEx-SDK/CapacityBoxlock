package com.androidex.capbox.ui.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.imageloader.RecyclerPauseOnScrollListener;
import com.androidex.capbox.base.imageloader.UILKit;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;

import java.util.List;

/**
 * @author liyp
 * @version 1.0.0
 * @description recyclerView
 * @createTime 2015/11/16
 * @editTime
 * @editor
 */
public class QTRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    public static final int LOAD_MORE_ITEM_SLOP = 2;
    View loadingView;
    View emptyView;
    View errorView;
    ViewStub loadingViewStub;
    ViewStub emptyViewStub;
    ViewStub errorViewStub;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private View contentView;

    private boolean loadMore = false;
    private int totalPage = 1;
    private int currentPage = 1;

    private int padding;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private int scrollbarStyle;
    private int loadingLayoutId;
    private int emptyLayoutId;
    private int errorLayoutId;
    private int backgroundColor;
    private boolean clipToPadding;
    private boolean scrollbarNone = false;

    private OnRefreshAndLoadMoreListener onRefreshAndLoadMoreListener;
    QTScrollCallback qtScrollCallback;

    QTRecyclerAdapter qtRecyclerAdapter;
    LayoutManagerType layoutManagerType;        //LayoutManager类型
    private int[] lastScrollPositions;      //瀑布流位置存储
    private int[] firstScrollPositions;

    public static final String DISPLAY_STATE = "display_state";
    public static final int STATE_CONTENT = 0x1;
    public static final int STATE_LOADING = 0x2;
    public static final int STATE_EMPTY = 0x3;
    public static final int STATE_ERROR = 0x4;
    private int displayState = STATE_LOADING;

    public QTRecyclerView(Context context) {
        super(context);
        initAttrs(context, null);
        initViews();
    }

    public QTRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initViews();
    }

    public QTRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initViews();
    }


    private void initAttrs(Context ctx, AttributeSet attr) {
        TypedArray ta = ctx.obtainStyledAttributes(attr, R.styleable.CustomRecyclerView);

        backgroundColor = ta.getColor(R.styleable.CustomRecyclerView_recyclerBackgroundColor, Color.WHITE);
        padding = (int) ta.getDimension(R.styleable.CustomRecyclerView_recyclerPadding, -1.0f);
        paddingLeft = (int) ta.getDimension(R.styleable.CustomRecyclerView_recyclerPaddingLeft, 0.0f);
        paddingRight = (int) ta.getDimension(R.styleable.CustomRecyclerView_recyclerPaddingRight, 0.0f);
        paddingTop = (int) ta.getDimension(R.styleable.CustomRecyclerView_recyclerPaddingTop, 0.0f);
        paddingBottom = (int) ta.getDimension(R.styleable.CustomRecyclerView_recyclerPaddingBottom, 0.0f);
        scrollbarStyle = ta.getInt(R.styleable.CustomRecyclerView_recyclerScrollbarStyle, 2);
        clipToPadding = ta.getBoolean(R.styleable.CustomRecyclerView_recyclerClipToPadding, false);
        scrollbarNone = ta.getBoolean(R.styleable.CustomRecyclerView_recyclerScrollbarNone, false);
        loadingLayoutId = ta.getResourceId(R.styleable.CustomRecyclerView_loadingLayoutId, R.layout.view_qt_rec_loading_view);
        emptyLayoutId = ta.getResourceId(R.styleable.CustomRecyclerView_emptyLayoutId, R.layout.view_qt_rec_empty_view);
        errorLayoutId = ta.getResourceId(R.styleable.CustomRecyclerView_errorLayoutId, R.layout.view_qt_rec_error_view);

        ta.recycle();
    }

    private void initViews() {
        inflate(getContext(), R.layout.view_qt_recyclerview, this);
        setPadding(0, 0, 0, 0);
        setClickable(true);

        errorViewStub = (ViewStub) findViewById(R.id.errorViewLayout);
        emptyViewStub = (ViewStub) findViewById(R.id.emptyViewLayout);
        loadingViewStub = (ViewStub) findViewById(R.id.loadingViewLayout);

        setErrorView(errorLayoutId);
        setEmptyView(emptyLayoutId);
        setLoadingView(loadingLayoutId);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        contentView = swipeRefreshLayout;
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(processMoreListener);
        if (padding != -1) {
            recyclerView.setPadding(padding, padding, padding, padding);
        } else {
            recyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        recyclerView.setClipToPadding(clipToPadding);

        if (scrollbarNone) {
            recyclerView.setVerticalScrollBarEnabled(false);
            recyclerView.setHorizontalScrollBarEnabled(false);
        } else {
            recyclerView.setScrollBarStyle(scrollbarStyle);
        }

        recyclerView.setBackgroundColor(backgroundColor);

        setDisplayState(STATE_LOADING);
    }

    public QTRecyclerView setErrorView(int errorResId) {
        errorViewStub.setLayoutResource(errorResId);
        errorView = errorViewStub.inflate();
        return this;
    }

    public QTRecyclerView setEmptyView(int emptyResId) {
        emptyViewStub.setLayoutResource(emptyResId);
        emptyView = emptyViewStub.inflate();
        return this;
    }

    public QTRecyclerView setLoadingView(int loadingResId) {
        loadingViewStub.setLayoutResource(loadingResId);
        loadingView = loadingViewStub.inflate();
        return this;
    }

    /**
     * 重新加载
     */
    public void retryLoad() {
        swipeRefreshLayout.setRefreshing(true);
        setDisplayState(STATE_CONTENT);
        if (getOnRefreshAndLoadMoreListener() != null) {
            getOnRefreshAndLoadMoreListener().onRefresh();
        }
    }

    public void notifyLoadFailed(Throwable error) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        loadMore = false;
        if (currentPage == 1 && recyclerView.getLayoutManager().getChildCount() == 0) {
            setDisplayState(STATE_ERROR);
        } else {
            setDisplayState(STATE_CONTENT);
//            CommonKit.showMsgShort(getContext(), error.getMessage());
        }

        TextView tv_error_message = (TextView) errorView.findViewById(R.id.tv_error_message);
        if (tv_error_message != null) {
            tv_error_message.setText(error.getMessage());
        }

    }

    private void loadMoreCompleted() {
        loadMore = false;
        swipeRefreshLayout.setEnabled(true);
        setDisplayState(STATE_CONTENT);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        if (getOnRefreshAndLoadMoreListener() != null) {
            getOnRefreshAndLoadMoreListener().onRefresh();
        }
    }

    public QTRecyclerView setPage(int currentPage, int totalPage) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        return this;
    }


    public void setDisplayState(int state) {
        if (qtRecyclerAdapter != null && qtRecyclerAdapter.getItemCount() > 0) {
            this.displayState = STATE_CONTENT;
            contentView.setVisibility(VISIBLE);
            loadingView.setVisibility(GONE);
            emptyView.setVisibility(GONE);
            errorView.setVisibility(GONE);
        } else {
            this.displayState = state;
            loadingView.setVisibility(state == STATE_LOADING ? VISIBLE : GONE);
            emptyView.setVisibility(state == STATE_EMPTY ? VISIBLE : GONE);
            errorView.setVisibility(state == STATE_ERROR ? VISIBLE : GONE);
            contentView.setVisibility(state == STATE_CONTENT ? VISIBLE : GONE);
        }

    }


    public QTRecyclerView setAdapter(final RecyclerView.Adapter adapter) {
        if (adapter == null) {
            RLog.e("adapter can not be null");
            return this;
        }
        if (!(adapter instanceof QTRecyclerAdapter)) {
            qtRecyclerAdapter = new QTRecyclerAdapter(adapter);
        } else {
            qtRecyclerAdapter = (QTRecyclerAdapter) adapter;
        }

        recyclerView.setAdapter(qtRecyclerAdapter);
        if (adapter.getItemCount() > 0) {
            setDisplayState(STATE_CONTENT);
        }
        qtRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            private void update() {
                int dataCount = qtRecyclerAdapter.getDataCount();
                if (dataCount > 0) {
                    if (loadMore) {
                        loadMoreCompleted();
                    }
                    setDisplayState(STATE_CONTENT);
                } else {
                    if (qtRecyclerAdapter.getHeaderSize() > 0 || qtRecyclerAdapter.getFooterSize() > 0) {

                    } else {
                        setDisplayState(STATE_EMPTY);
                    }
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return this;
    }


    RecyclerView.OnScrollListener processMoreListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (qtRecyclerAdapter == null || recyclerView.getLayoutManager() == null) return;

            int totalCount = qtRecyclerAdapter.getItemCount();

            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && !loadMore
                    && getLastVisibleItemPosition(recyclerView.getLayoutManager()) + LOAD_MORE_ITEM_SLOP > totalCount
                    && totalPage > currentPage) {
                loadMore = true;
                if (getOnRefreshAndLoadMoreListener() != null) {
                    getOnRefreshAndLoadMoreListener().onLoadMore(++currentPage);
                    swipeRefreshLayout.setEnabled(false);
                }
            }else{
                swipeRefreshLayout.setEnabled(true);
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (qtRecyclerAdapter == null || recyclerView.getLayoutManager() == null || getQtScrollCallback() == null)
                return;
            if (getLastVisibleItemPosition(recyclerView.getLayoutManager()) == qtRecyclerAdapter.getItemCount() - 1) {
                getQtScrollCallback().onBottom();
            }
            if (getFirstVisibleItemPosition(recyclerView.getLayoutManager()) == 0) {
                getQtScrollCallback().onTop();
            }

        }
    };


    private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastScrollPositions == null)
                    lastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                staggeredGridLayoutManager.findLastVisibleItemPositions(lastScrollPositions);
                lastVisibleItemPosition = findMax(lastScrollPositions);
                break;
        }
        return lastVisibleItemPosition;
    }


    private int getFirstVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        int firstVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LINEAR:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case GRID:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastScrollPositions == null)
                    lastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                staggeredGridLayoutManager.findLastVisibleItemPositions(firstScrollPositions);
                firstVisibleItemPosition = findMin(firstScrollPositions);
                break;
        }
        return firstVisibleItemPosition;
    }


    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] positions) {
        int min = Integer.MIN_VALUE;
        for (int value : positions) {
            if (value < min)
                min = value;
        }
        return min;
    }

    /**
     * 添加headerView
     *
     * @param position
     * @param view
     * @return
     */
    public boolean addHeaderView(int position, View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.addHeadView(position, view);
        }
        return result;
    }

    /**
     * 添加headerView
     *
     * @param view
     * @return
     */
    public boolean addHeaderView(View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.addHeadView(view);
        }
        return result;
    }

    /**
     * 删除headerView
     *
     * @param view
     * @return
     */
    public boolean removeHeaderView(View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.removeHeadView(view);
        }
        return result;
    }

    /**
     * 添加footerView
     *
     * @param view
     * @return
     */
    public boolean addFooterView(View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.addFootView(view);
        }
        return result;
    }


    /**
     * 添加footerView
     *
     * @param position
     * @param view
     * @return
     */
    public boolean addFooterView(int position, View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.addFootView(position, view);
        }
        return result;
    }

    /**
     * 删除footerView
     *
     * @param view
     * @return
     */
    public boolean removeFooterView(View view) {
        boolean result = false;
        if (view == null) {
            return result;
        }
        if (qtRecyclerAdapter != null) {
            result = qtRecyclerAdapter.removeFootView(view);
        }
        return result;
    }

    /**
     * 获取headerView的数量
     *
     * @return
     */
    public int getHeaderCount() {
        if (qtRecyclerAdapter != null) {
            return qtRecyclerAdapter.getHeaderSize();
        }
        return 0;
    }

    /**
     * 获取headerViews
     *
     * @return
     */
    public List<View> getHeaderViewList() {
        if (qtRecyclerAdapter != null) {
            return qtRecyclerAdapter.getHeaderViewList();
        }
        return null;
    }

    /**
     * 获取footerView的数量
     *
     * @return
     */
    public int getFooterCount() {
        if (qtRecyclerAdapter != null) {
            return qtRecyclerAdapter.getFooterSize();
        }
        return 0;
    }

    /**
     * 获取footerView
     *
     * @return
     */
    public List<View> getFooterViewList() {
        if (qtRecyclerAdapter != null) {
            return qtRecyclerAdapter.getFooterViewList();
        }
        return null;
    }

    public QTRecyclerView addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
        return this;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.state = this.displayState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.displayState = savedState.state;
        setDisplayState(this.displayState);
    }

    public void setQtScrollCallback(QTScrollCallback qtScrollCallback) {
        this.qtScrollCallback = qtScrollCallback;
    }

    public QTScrollCallback getQtScrollCallback() {
        return qtScrollCallback;
    }

    public QTRecyclerAdapter getQtRecyclerAdapter() {
        return qtRecyclerAdapter;
    }

    public QTRecyclerView setOnRefreshAndLoadMoreListener(OnRefreshAndLoadMoreListener onRefreshAndLoadMoreListener) {
        this.onRefreshAndLoadMoreListener = onRefreshAndLoadMoreListener;
        swipeRefreshLayout.setEnabled(true);
        return this;
    }

    public OnRefreshAndLoadMoreListener getOnRefreshAndLoadMoreListener() {
        return onRefreshAndLoadMoreListener;
    }

    public QTRecyclerView setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
        return this;
    }

    public QTRecyclerView setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null) {
            throw new IllegalArgumentException("LayoutManager can not be null.");
        }
        recyclerView.setLayoutManager(layoutManager);

        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            setSpanLookUp(layoutManager, spanCount);
        }

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            int spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            setSpanLookUp(layoutManager, spanCount);
        }
        return this;
    }

    public QTRecyclerView setHasFixedSize(boolean hasFixedSize) {
        recyclerView.setHasFixedSize(hasFixedSize);
        return this;
    }

    public QTRecyclerView defaultNoDivider() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        return this;
    }

    public QTRecyclerView defaultUseDivider(Context context) {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                        .colorResId(R.color.divider_color)
                        .size(CommonKit.dpToPxInt(context, 0.5f))
                        .build()
        );
        return this;
    }

    public QTRecyclerView defaultUseDivider12(Context context) {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                        .colorResId(R.color.bg_gray)
                        .size(12)
                        .build()
        );
        return this;
    }


    public QTRecyclerView setRefreshEnable(boolean enable) {
        swipeRefreshLayout.setEnabled(enable);
        return this;
    }

    public QTRecyclerView verticalLayoutManager(Context context) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(manager);
        return this;
    }

    public QTRecyclerView horizontalLayoutManager(Context context) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(manager);
        return this;
    }

    public QTRecyclerView gridLayoutManager(Context context, int spanCount) {
        GridLayoutManager manager = new GridLayoutManager(context, spanCount);
        setLayoutManager(manager);
        return this;
    }

    /**
     * 滚动时停止加载图片
     *
     * @return
     */
    public QTRecyclerView ImageLoaderPauseOnScroll() {
        RecyclerPauseOnScrollListener listener = new RecyclerPauseOnScrollListener(UILKit.getLoader());
        recyclerView.addOnScrollListener(listener);
        return this;
    }


    private void setSpanLookUp(RecyclerView.LayoutManager layoutManager, final int spanCount) {

        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (qtRecyclerAdapter != null) {
                    return qtRecyclerAdapter.isHeaderOrFooter(position) ? spanCount : 1;
                }
                return GridLayoutManager.DEFAULT_SPAN_COUNT;
            }
        });
    }

    public interface OnRefreshAndLoadMoreListener {
        void onRefresh();

        void onLoadMore(int page);
    }


    public View getLoadingView() {
        return loadingView;
    }

    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置默认emptyView元素
     *
     * @param emptyIcon
     * @param emptyText
     * @return
     */
    public QTRecyclerView setEmptyViewDefault(int emptyIcon, String emptyText) {
        ImageView iv_empty_emotion = (ImageView) emptyView.findViewById(R.id.iv_empty_emotion);
        TextView tv_empty_message = (TextView) emptyView.findViewById(R.id.tv_empty_message);

        iv_empty_emotion.setImageResource(emptyIcon);
        tv_empty_message.setText(emptyText);

        return this;
    }

    public View getErrorView() {
        return errorView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    enum LayoutManagerType {
        LINEAR, GRID, STAGGERED_GRID
    }


    static class SavedState extends BaseSavedState {
        private int state;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            try {
                state = source.readInt();
            } catch (IllegalArgumentException e) {
                state = STATE_LOADING;
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
