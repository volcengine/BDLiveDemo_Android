package com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bytedance.live.common.interfaces.Consumer;
import com.bytedance.live.common.utils.SizeUtils;
import com.bytedance.live.common.utils.StringUtils;
import com.bytedance.live.sdk.player.view.RoundTextView;
import com.bytedance.videoarch.ttlivestream.R;
import com.bytedance.videoarch.ttlivestream.databinding.DialogPlayerResolutionLandBinding;

public class TVUResolutionSettingLandDialog extends Dialog {

    String[] resolutions;
    String selectedResolution;
    Consumer<String> changeResolutionListener;

    Context context;
    DialogPlayerResolutionLandBinding binding;
    View.OnClickListener onClickListener = v -> {
        String resolution = (String) v.getTag();
        if (changeResolutionListener != null) {
            changeResolutionListener.accept(resolution);
        }
        dismiss();
    };

    public TVUResolutionSettingLandDialog(@NonNull Context context,
                                          String[] resolutions, String selectedResolution) {
        super(context, R.style.TvuLiveBottomOverlapDialog);
        this.context = context;
        this.resolutions = resolutions;
        this.selectedResolution = selectedResolution;

        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_player_resolution_land,
                new FrameLayout(context), false);
        setContentView(binding.getRoot());
        configDialogStyle();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateLinearLayout();
    }

    private void configDialogStyle() {
        Window window = getWindow();
        window.setWindowAnimations(com.bytedance.live.sdk.R.style.TVULandDialogAnimation);
        window.setGravity(Gravity.END);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = SizeUtils.dp2px(268);
        // 定制高度
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding.getRoot().setPadding(0, 0, 0, 0);
        window.setAttributes(lp);
    }


    private void initView() {
        binding.resolutionTitleText.setText("清晰度选择");
    }

    private void updateLinearLayout() {
        binding.resolutionLinearlayout.removeAllViews();
        binding.resolutionLinearlayout.post(() -> {
            for (int i = 0; i < resolutions.length; i++) {
                String resolution = resolutions[i];

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        SizeUtils.dp2px(161),
                        SizeUtils.dp2px(36)
                );
                lp.topMargin = i != 0 ? SizeUtils.dp2px(16) : 0;

                RoundTextView roundTextView = new RoundTextView(getContext());
                roundTextView.setGravity(Gravity.CENTER);
                roundTextView.setTextSize(15);
                roundTextView.setText(ResolutionConst.readableResolutionString(resolution));
                roundTextView.setTag(resolution);
                roundTextView.setRadius(SizeUtils.dp2px(4));
                roundTextView.setOnClickListener(onClickListener);

                binding.resolutionLinearlayout.addView(roundTextView, lp);
            }
            updateSelectedResolution();
        });
    }

    private void updateSelectedResolution() {
        for (int i = 0; i < binding.resolutionLinearlayout.getChildCount(); i++) {
            RoundTextView child = (RoundTextView) binding.resolutionLinearlayout.getChildAt(i);
            String resolution = (String) child.getTag();
            if (StringUtils.equals(resolution, selectedResolution)) {
                child.setBackGroundColor(Color.parseColor("#4D4086FF"));
                child.setTextColor(Color.parseColor("#94C2FF"));
            } else {
                child.setBackGroundColor(Color.parseColor("#29FFFFFF"));
                child.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    public void setChangeResolutionListener(Consumer<String> changeResolutionListener) {
        this.changeResolutionListener = changeResolutionListener;
    }
}
