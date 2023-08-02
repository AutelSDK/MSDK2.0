package com.autel.sdk.debugtools.view.spinnerview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.autel.sdk.debugtools.DisplayUtil;
import com.autel.sdk.debugtools.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author peiguo.chen
 * @date 2021/8/18
 * 公共选择popwindow
 */
public class DebugSpinnerView extends LinearLayout implements View.OnClickListener {
    private static final int POPUP_WINDOW_ITEM_HEIGHT = 32;
    private final ConstraintLayout mContainer;
    private final TextView mSpinnerTv;
    private final ImageView mIcon;
    private final List<String> dataList = new ArrayList<>();
    private final Context mContext;
    private View mRecyclerContainer;
    private PopupWindow mPopupWindow;
    private DebugSpinnerAdapter mAdapter;
    private SpinnerViewListener listener;
    private SpinnerBeforeViewChangeListener beforeViewChangeListener;
    private int mPopupWindowHeight;
    private int mPopupWindowMaxHeight;
    private boolean isEnable = true;
    private long curTime = 0L;//点击时间

    public DebugSpinnerView(Context context) {
        this(context, null);
    }

    public DebugSpinnerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugSpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = View.inflate(context, R.layout.view_spiner, this);
        mContainer = view.findViewById(R.id.rl_container);
        mSpinnerTv = view.findViewById(R.id.tv_spinner);
        mIcon = view.findViewById(R.id.icon);
        mContainer.setOnClickListener(this);
        initPopupWindow();
    }

    public void setContainerWidth(int width) {
        // 不要改这里，会崩溃
        LayoutParams params = new LayoutParams(DisplayUtil.dip2px(width), DisplayUtil.dip2px(POPUP_WINDOW_ITEM_HEIGHT));
        mContainer.setLayoutParams(params);
    }

    public void setDefaultText(int position) {
        if (dataList.size() != 0 && dataList.size() > position && position != -1) {
            mSpinnerTv.setText(dataList.get(position));
        }
        if (mAdapter != null) mAdapter.setDataList(dataList, mSpinnerTv.getText().toString());
    }

    public void setDefaultText(String str) {
        mSpinnerTv.setText(str);
        if (mAdapter != null) mAdapter.setDataList(dataList, mSpinnerTv.getText().toString());
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> list) {
        dataList.clear();
        dataList.addAll(list);
        if (mAdapter != null) mAdapter.setDataList(dataList, mSpinnerTv.getText().toString());
    }

    private void initPopupWindow() {
        mRecyclerContainer = LayoutInflater.from(getContext()).inflate(R.layout.sliding_drop_press_view, null);
        mRecyclerContainer.setBackgroundResource(R.drawable.debug_shape_list_item_default_4);
        RecyclerView recyclerView = mRecyclerContainer.findViewById(R.id.rv_spinner);
        recyclerView.addItemDecoration(new MissionDropViewListItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DebugSpinnerAdapter(dataList);
        mAdapter.setOnItemClickListener(position -> {
            if (position == -1 || position >= dataList.size())
                return;
            mPopupWindow.dismiss();
            if (beforeViewChangeListener != null) {
                if (mAdapter.getChoosePosition() != position) {
                    beforeViewChangeListener.onSelectPosition(position);
                }
                return;
            }
            mSpinnerTv.setText(dataList.get(position));
            mAdapter.setChoosePosition(position);
            mAdapter.notifyDataSetChanged();
            if (listener != null) {
                listener.onSelectPosition(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        mPopupWindow = new PopupWindow(mRecyclerContainer);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mIcon.setImageDrawable(mContext.getDrawable(R.drawable.debug_ic_outlined_arrow_down_white));
                curTime = System.currentTimeMillis();
            }
        });
    }

    public void setSpinnerViewListener(SpinnerViewListener listener) {
        this.listener = listener;
    }

    public void setSpinnerBeforeViewChangeListener(SpinnerBeforeViewChangeListener beforeViewChangeListener) {
        this.beforeViewChangeListener = beforeViewChangeListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_container) {
            //防止事件穿透
            if (System.currentTimeMillis() - curTime < 200) return;
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                if (isEnable) showPopupView();
            }
        }
    }

    private void showPopupView() {
        mIcon.setImageDrawable(mContext.getDrawable(R.drawable.debug_ic_outlined_arrow_up_white));
        int width = mContainer.getMeasuredWidth();
        int curWindowHeight;
        int height = dataList.size() * mContainer.getMeasuredHeight();
        mPopupWindowMaxHeight = 5 * mContainer.getMeasuredHeight();
        mPopupWindow.setWidth(width);
        setDropdownHeight(height);
        if (height > mPopupWindowMaxHeight) {
            curWindowHeight = mPopupWindowMaxHeight;
        } else {
            curWindowHeight = height;
        }
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);

        int[] location = new int[2];
        mContainer.getLocationOnScreen(location);

        int[] locationWindow = new int[2];
        mContainer.getLocationInWindow(locationWindow);

        if (location[1] + curWindowHeight > rect.bottom) {
            int count = Math.min(dataList.size(), 5);
            mPopupWindow.showAtLocation(mContainer, Gravity.NO_GRAVITY, locationWindow[0], locationWindow[1] + getHeight() - count * POPUP_WINDOW_ITEM_HEIGHT);
        } else {
            mPopupWindow.showAtLocation(mContainer, Gravity.NO_GRAVITY, locationWindow[0], locationWindow[1] + mContainer.getMeasuredHeight() + 10);
        }
    }

    /**
     * 控件是否可用
     *
     * @param enable
     */
    public void isEnable(boolean enable) {
        isEnable = enable;
        mSpinnerTv.setTextColor(ContextCompat.getColor(mContext, enable ? R.color.debug_color_white : R.color.debug_color_7f_ff));
    }

    private void setDropdownHeight(int height) {
        mPopupWindowHeight = height;
        mPopupWindow.setHeight(calculatePopupWindowHeight());
    }

    private int calculatePopupWindowHeight() {
        if (mAdapter == null) {
            return WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (mPopupWindowMaxHeight > 0 && mPopupWindowHeight > mPopupWindowMaxHeight) {
            return mPopupWindowMaxHeight;
        } else if (mPopupWindowHeight != WindowManager.LayoutParams.MATCH_PARENT
                && mPopupWindowHeight != WindowManager.LayoutParams.WRAP_CONTENT) {
            return mPopupWindowHeight;
        }
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public interface SpinnerViewListener {
        /**
         * 选中回调
         *
         * @param position 选中项
         */
        void onSelectPosition(int position);
    }

    public interface SpinnerBeforeViewChangeListener {
        /**
         * 选中回调，但不触发UI变化
         *
         * @param position 选中项
         */
        void onSelectPosition(int position);
    }

}
