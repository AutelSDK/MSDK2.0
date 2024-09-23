package com.autel.sdk.debugtools;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.autel.drone.demo.R;
import com.autel.sdk.debugtools.fragment.KeyItemActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * key item adapter for key capabilities and actions list item
 * Copyright: Autel Robotics
 *
 * @author maowei on 2022/12/17.
 */
public class KeyItemAdapter extends RecyclerView.Adapter<KeyItemAdapter.ComViewHolder> implements Filterable {

    private final String packageName;
    protected List<KeyItem<?, ?>> dataList;
    protected List<KeyItem<?, ?>> mFilterList;
    protected Context context;
    private KeyItemActionListener<KeyItem<?, ?>> callback;


    public KeyItemAdapter(Context context, List<KeyItem<?, ?>> dataList, KeyItemActionListener<KeyItem<?, ?>> callback) {
        this.context = context;
        this.dataList = dataList;
        this.mFilterList = dataList;
        this.callback = callback;
        packageName = context.getPackageName();
    }

    @Override
    public ComViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComViewHolder(LayoutInflater.from(context).inflate(R.layout.item_camera_key_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ComViewHolder holder, int position) {
        convert(holder, mFilterList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilterList == null ? 0 : mFilterList.size();
    }

    public void convert(ComViewHolder viewHolder, final KeyItem<?, ?> keyItem) {
        if (viewHolder == null || keyItem == null) {
            return;
        }
        TextView textView = viewHolder.getView(R.id.tv_item_name);

        try {
            String id = "debug_key_list_" + keyItem.name.toLowerCase().replace("_", "");
            int i = context.getResources().getIdentifier(id, "string", packageName);
            textView.setText(context.getString(i));
        } catch (Exception ex) {
            textView.setText(keyItem.name);
        }
        /*boolean isEnglish = SDKStorage.INSTANCE.getIntValue(MmkvConstants.IS_ENGLISH, 0) == 0;
        if (isEnglish) {
            textView.setText(keyItem.name);
        } else {
            textView.setText(ChineseConstants.INSTANCE.getChineseKey(keyItem.keyInfo.keyName));
        }*/
        if (keyItem.isItemSelected()) {
            textView.setBackgroundColor(Color.GRAY);
        } else {
            textView.setBackgroundColor(Color.TRANSPARENT);
        }
        textView.setOnClickListener(v -> {
            if (callback != null) {
                callback.actionChange(keyItem);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilterList = dataList;
                } else {
                    List<KeyItem<?, ?>> filteredList = new ArrayList<>();
                    for (KeyItem<?, ?> item : dataList) {
                        String lowStr = charString.toLowerCase();
                        if (item.name.toLowerCase().contains(lowStr) || item.keyInfo.keyName.toLowerCase().contains(lowStr)) {
                            filteredList.add(item);
                        }
                    }
                    mFilterList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterList = (ArrayList<KeyItem<?, ?>>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * 缓存容器
     */
    public class ComViewHolder extends RecyclerView.ViewHolder {

        private View convertView;
        private SparseArrayCompat<View> views;

        public ComViewHolder(View itemView) {
            super(itemView);
            this.convertView = itemView;
            this.views = new SparseArrayCompat<>();
        }


        public <T extends View> T getView(int layoutId) {
            View view = views.get(layoutId);
            if (view == null) {
                view = convertView.findViewById(layoutId);
                views.put(layoutId, view);
            }
            return (T) view;
        }
    }

}
