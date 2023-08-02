package com.autel.sdk.debugtools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.autel.drone.sdk.vmodelx.SDKManager;
import com.autel.sdk.debugtools.fragment.KeyItemActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * key and it's values dialog creator class
 * Copyright: Autel Robotics
 *
 * @author maowei on 2022/12/17.
 */
public class KeyValueDialogUtil {

    private static final int LIST_Y_OFF_SET = 3;

    private KeyValueDialogUtil() {
        // init something
    }

    /**
     * 显示单选对话框
     */
    public static void showSingleChoiceDialog(Context context, List<String> data, int selectedIndex, final KeyItemActionListener<List<String>> callBack) {
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = data.toArray(new String[data.size()]);
        builder.setSingleChoiceItems(items, selectedIndex, (dialog1, which) -> {
            if (callBack != null) {
                callBack.actionChange(Arrays.asList(items[which]));
                dialog1.dismiss();
            }
        });
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Display checkboxes items dialog box
     */
    public static void showMultiChoiceDialog(Context context, List<String> data, final KeyItemActionListener<List<String>> callBack) {
        AlertDialog dialog;
        List<String> values = new ArrayList<>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = data.toArray(new String[data.size()]);
        builder.setMultiChoiceItems(items, null, (dialog1, which, isChecked) -> {
            if (isChecked) {
                values.add(items[which]);
            } else {
                values.remove(items[which]);
            }

        });
        builder.setPositiveButton(R.string.debug_common_text_confirm, (dialog12, which) -> callBack.actionChange(values));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

    }

    /**
     * 显示简单列表弹窗
     *
     * @param anchor
     * @param data
     * @param callback
     */
    public static void showListConfirmWindow(View anchor, final List<String> data, String title, final KeyItemActionListener<String> callback) {
        if (anchor == null || anchor.getContext() == null) {
            return;
        }
        Context context = anchor.getContext();
        View rootView = View.inflate(context, R.layout.dialog_list_confirm, null);
        final PopupWindow window = new PopupWindow(context);
        window.setWidth(Util.getHeight(SDKManager.get().getSContext()) / 2);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(false);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setBackgroundDrawable(new ColorDrawable(0xffffff));
        window.setContentView(rootView);
        window.showAsDropDown(anchor, 0, LIST_Y_OFF_SET, Gravity.CENTER | Gravity.BOTTOM);

        ListView listView = rootView.findViewById(R.id.list_view);
        TextView titleView = rootView.findViewById(R.id.title);
        if (Util.isNotBlank(title)) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_textview, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> callback.actionChange(data.get(position)));
        rootView.findViewById(R.id.button).setOnClickListener(v -> {
            window.dismiss();
            callback.actionChange(context.getString(R.string.debug_common_text_confirm));
        });
    }

    /**
     * 显示输入确认弹窗
     *
     * @param context
     * @param item
     */
    public static void showInputDialog(Activity context, KeyItem<?, ?> item, final KeyItemActionListener<String> callback) {
        showInputDialog(context, context.getString(R.string.debug_setup_title) + item.getName() + "：", item.getParamJsonStr(), "", false, callback);
    }


    /**
     * 显示输入确认弹窗
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showInputDialog(Activity context, String title, final String msg, String hint, boolean singleLine, final KeyItemActionListener<String> callback) {
        View dialogView = context.getLayoutInflater().inflate(R.layout.dialog_param_input, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialogView.setBackgroundColor(context.getResources().getColor(R.color.debug_gray, null));
        }

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView tvTitle = dialogView.findViewById(R.id.title);
        tvTitle.setText(title);

        final EditText input = dialogView.findViewById(R.id.input);
        input.setSingleLine(singleLine);
        if (Util.isNotBlank(msg)) {
            input.setText(msg);
        }
        if (Util.isNotBlank(hint)) {
            input.setHint(hint);
        }
        input.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogView.findViewById(R.id.confirm).setOnClickListener(v -> {
            if (callback != null) {
                callback.actionChange(input.getText().toString().trim());
            }
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }


    /**
     * 显示通用弹窗
     *
     * @param anchor
     * @param data
     * @param callback
     */
    public static void showFilterListWindow(View anchor, final List<KeyItem<?, ?>> data, final KeyItemActionListener<KeyItem<?, ?>> callback) {
        if (anchor == null || anchor.getContext() == null) {
            return;
        }
    }


    /**
     * 显示通用弹窗
     *
     * @param anchor
     * @param data
     * @param callback
     */
    public static void showChannelFilterListWindow(View anchor, final List<ChannelType> data,
                                                   final KeyItemActionListener<ChannelType> callback) {
        if (anchor == null || anchor.getContext() == null) {
            return;
        }
        Context context = anchor.getContext();
        View rootView = View.inflate(context, R.layout.window_simple_listview, null);
        final PopupWindow window = new PopupWindow(context);
        window.setWidth(Util.getHeight(SDKManager.get().getSContext()) / 2);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setBackgroundDrawable(new ColorDrawable(0xffffff));

        window.setContentView(rootView);
        window.showAsDropDown(anchor, (int) (anchor.getWidth() * 1.5), -DisplayUtil.dip2px(37), Gravity.LEFT | Gravity.BOTTOM);
        ListView listView = rootView.findViewById(R.id.list_view);

        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            String name = data.get(i).name();
            try {
                String packageName = context.getPackageName();
                ChannelType enumeration = Enum.valueOf(ChannelType.class, name);
                String id = "debug_" + enumeration.name().toLowerCase(Locale.getDefault())
                        .replace(" ", "")
                        .replace("_", "")
                        .replace("channeltype", "channel_type_");
                if (!id.contains("debug_channel_type_")) {
                    id = id.replace("debug_", "debug_channel_type_");
                }
                int identifier = context.getResources().getIdentifier(id, "string", packageName);
                name = context.getResources().getString(identifier);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            titleList.add(name);
        }

//        ChannelTypeViewAdapter adapter = new ChannelTypeViewAdapter(context, titleList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_textview, titleList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (callback != null) {
                callback.actionChange(data.get(position));
            }
            if (window != null) {
                window.dismiss();
            }
        });

        EditText filter = rootView.findViewById(R.id.et_filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // dosomething
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // dosomething
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });


    }

    /**
     * Display alert dialog box with confirm btn only
     */

    public static void showNormalDialog(Activity context, String title) {
        View dialogView = context.getLayoutInflater().inflate(R.layout.dialog_tips, null);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView tvTitle = dialogView.findViewById(R.id.title);
        tvTitle.setText(title);
        dialogView.findViewById(R.id.confirm).setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

}