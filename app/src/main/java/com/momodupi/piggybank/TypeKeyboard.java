package com.momodupi.piggybank;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TypeKeyboard {

    private Activity activity;
    private EditText editText;

    private View panelView;

    private View contentView;

    private InputMethodManager inputMethodManager;

    private SharedPreferences sharedPreferences;

    private static final String EMOJI_KEYBOARD = "Keyboard";

    private static final String KEY_SOFT_KEYBOARD_HEIGHT = "SoftKeyboardHeight";

    private static final int SOFT_KEYBOARD_HEIGHT_DEFAULT = 654;

    private OnPanelVisibilityChangeListener panelVisibilityChangeListener;

    private Handler handler;

    TypeKeyboard(Activity activity, EditText editText, View PanelView, View PanelSwitchView, View contentView) {
        init(activity, editText, PanelView, PanelSwitchView, contentView);
    }

    private void init(Activity activity, EditText editText, View PanelView, View PanelSwitchView, View contentView) {
        this.activity = activity;
        this.editText = editText;
        this.panelView = PanelView;
        this.contentView = contentView;

        this.editText.performClick();
        this.editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && TypeKeyboard.this.panelView.isShown()) {
                    lockContentViewHeight();
                    hidePanel(true);
                    unlockContentViewHeight();
                }
                return false;
            }
        });

        this.contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (TypeKeyboard.this.panelView.isShown()) {
                        hidePanel(false);
                    }
                    else if (isSoftKeyboardShown()) {
                        hideSoftKeyboard();
                    }
                }
                return false;
            }
        });


        PanelSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TypeKeyboard.this.panelView.isShown()) {
                    lockContentViewHeight();
                    hidePanel(true);
                    unlockContentViewHeight();
                }
                else {
                    if (isSoftKeyboardShown()) {
                        lockContentViewHeight();
                        showPanel();
                        unlockContentViewHeight();
                    } else {
                        showPanel();
                    }
                }
            }
        });

        this.inputMethodManager = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.sharedPreferences = this.activity.getSharedPreferences(EMOJI_KEYBOARD, Context.MODE_PRIVATE);
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.handler = new Handler();
        init();
    }

    private void init() {
        if (!sharedPreferences.contains(KEY_SOFT_KEYBOARD_HEIGHT)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftKeyboard(true);
                }
            }, 200);
        }
    }


    boolean interceptBackPress() {
        if (panelView.isShown()) {
            hidePanel(false);
            return true;
        }
        return false;
    }


    private void lockContentViewHeight() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        layoutParams.height = contentView.getHeight();
        layoutParams.weight = 0;
    }

    private void unlockContentViewHeight() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) contentView.getLayoutParams()).weight = 1;
            }
        }, 200);
    }

    private int getSoftKeyboardHeight() {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        int displayHeight = rect.bottom - rect.top;

        int availableHeight = ScreenUtils.getAvailableScreenHeight(activity);

        int softInputHeight = availableHeight - displayHeight - ScreenUtils.getStatusBarHeight(activity);

        if (softInputHeight != 0) {
            sharedPreferences.edit().putInt(KEY_SOFT_KEYBOARD_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }

    private int getSoftKeyboardHeightLocalValue() {
        return sharedPreferences.getInt(KEY_SOFT_KEYBOARD_HEIGHT, SOFT_KEYBOARD_HEIGHT_DEFAULT);
    }

    private boolean isSoftKeyboardShown() {
        return getSoftKeyboardHeight() != 0;
    }

    private void showSoftKeyboard(boolean saveSoftKeyboardHeight) {
        editText.requestFocus();
        inputMethodManager.showSoftInput(editText, 0);
        if (saveSoftKeyboardHeight) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSoftKeyboardHeight();
                }
            }, 200);
        }
    }

    private void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void showPanel() {
        int softKeyboardHeight = getSoftKeyboardHeight();
        if (softKeyboardHeight == 0) {
            softKeyboardHeight = getSoftKeyboardHeightLocalValue();
        }
        else {
            hideSoftKeyboard();
        }
        panelView.getLayoutParams().height = softKeyboardHeight;
        panelView.setVisibility(View.VISIBLE);
        if (panelVisibilityChangeListener != null) {
            panelVisibilityChangeListener.onShowPanel();
        }
    }

    private void hidePanel(boolean showSoftKeyboard) {
        if (panelView.isShown()) {
            panelView.setVisibility(View.GONE);
            if (showSoftKeyboard) {
                showSoftKeyboard(false);
            }
            if (panelVisibilityChangeListener != null) {
                panelVisibilityChangeListener.onHidePanel();
            }
        }
    }

    public interface OnPanelVisibilityChangeListener {

        void onShowPanel();

        void onHidePanel();
    }



    /*
    public void setPanelVisibilityChangeListener(OnPanelVisibilityChangeListener PanelVisibilityChangeListener) {
        this.panelVisibilityChangeListener = PanelVisibilityChangeListener;
    }

     */
}
