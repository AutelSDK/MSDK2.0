package com.autel.sdk.debugtools.view.spinnerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ScrollTextView extends AppCompatTextView {
    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
