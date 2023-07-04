package com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bytedance.live.common.utils.BarUtils;
import com.bytedance.live.sdk.util.UIUtil;

public class FullScreenHelper {
    private boolean isFullScreen;
    private ViewGroup oldParent;
    private int oldChildIndex;
    private ViewGroup.LayoutParams oldLayoutParams;

    private int oldDecorUIVisibility;
    private boolean oldIsStatusBarVisible;

    private final View fullScreenChildView;
    private final Activity activity;

    public FullScreenHelper(View fullScreenChildView) {
        this.fullScreenChildView = fullScreenChildView;
        this.activity = (Activity) fullScreenChildView.getContext();
        initData();
    }

    private void initData() {
        if (activity == null) {
            return;
        }
        oldDecorUIVisibility = activity.getWindow().getDecorView().getWindowSystemUiVisibility();
        oldIsStatusBarVisible = BarUtils.isStatusBarVisible(activity);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void changeFullScreen(boolean fullScreen) {
        if (activity == null) {
            return;
        }
        if (isFullScreen == fullScreen) {
            return;
        }
        if (fullScreen) {
            FrameLayout contentLayout = activity.findViewById(android.R.id.content);

            //先保存之前的parent
            oldParent = (ViewGroup) fullScreenChildView.getParent();
            oldChildIndex = oldParent.indexOfChild(fullScreenChildView);
            oldLayoutParams = fullScreenChildView.getLayoutParams();
            UIUtil.removeViewFromParent(fullScreenChildView);
            contentLayout.addView(fullScreenChildView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            BarUtils.setStatusBarVisibility(activity, false);
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            activity.setRequestedOrientation(requestedOrientation);


        } else {
            UIUtil.removeViewFromParent(fullScreenChildView);
            if (oldParent != null) {
                int addIndex = Math.max(0, Math.min(oldChildIndex, oldParent.getChildCount()-1));
                oldParent.addView(fullScreenChildView, addIndex, oldLayoutParams);
            }
            BarUtils.setStatusBarVisibility(activity, oldIsStatusBarVisible);
            activity.getWindow().getDecorView().setSystemUiVisibility(oldDecorUIVisibility);

            int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            activity.setRequestedOrientation(requestedOrientation);
        }
        this.isFullScreen = fullScreen;
    }


}
