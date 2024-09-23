package com.autel.sdk.debugtools.view.spinnerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.autel.drone.demo.R;

import java.util.ArrayList;
import java.util.List;

public class DebugSpinnerAdapter extends RecyclerView.Adapter<DebugSpinnerAdapter.SpinnerViewHolder> {
    private final List<String> dataList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private boolean showArrow;
    private int choosePosition;

    public DebugSpinnerAdapter(List<String> list) {
        dataList.clear();
        dataList.addAll(list);
    }

    public void setDataList(List<String> list, String localString) {
        dataList.clear();
        dataList.addAll(list);
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).equals(localString)) {
                choosePosition = i;
            }
        }
        notifyDataSetChanged();
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int position) {
        choosePosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_spinner_item, parent, false);
        return new SpinnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerViewHolder holder, int position) {
        holder.mSpinnerTv.setText(dataList.get(position));
        holder.mArrowIv.setVisibility(position == 0 && showArrow ? View.VISIBLE : View.GONE);
        if (choosePosition == position) {
            holder.mSpinnerTv.setTextColor(0xFFFEE15D);
            holder.mArrowIv.setVisibility(View.VISIBLE);
        } else {
            holder.mSpinnerTv.setTextColor(0xFFFFFFFF);
            holder.mArrowIv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public void showArrow(boolean showArrow) {
        this.showArrow = showArrow;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class SpinnerViewHolder extends RecyclerView.ViewHolder {
        TextView mSpinnerTv;
        ImageView mArrowIv;

        SpinnerViewHolder(View itemView) {
            super(itemView);
            mSpinnerTv = itemView.findViewById(R.id.tv_spinner);
            mArrowIv = itemView.findViewById(R.id.iv_arrow);
            itemView.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
