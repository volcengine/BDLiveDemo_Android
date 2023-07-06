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
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bytedance.live.common.interfaces.Consumer;
import com.bytedance.live.common.utils.SizeUtils;
import com.bytedance.live.common.utils.StringUtils;
import com.bytedance.live.sdk.player.view.RoundTextView;
import com.bytedance.videoarch.ttlivestream.R;
import com.bytedance.videoarch.ttlivestream.databinding.DialogPlayerResolutionPortraitBinding;
import com.ss.ttvideoengine.utils.ScreenUtils;

public class TVUResolutionSettingDialog extends Dialog {

    String[] resolutions;
    String selectedResolution;
    Consumer<String> changeResolutionListener;

    Context context;
    DialogPlayerResolutionPortraitBinding binding;
    View.OnClickListener onClickListener = v -> {
        String resolution = (String) v.getTag();
        if (changeResolutionListener != null) {
            changeResolutionListener.accept(resolution);
        }
        dismiss();
    };

    public TVUResolutionSettingDialog(@NonNull Context context,
                                      String[] resolutions, String selectedResolution) {
        super(context, R.style.TvuLiveBottomOverlapDialog);
        this.context = context;
        this.resolutions = resolutions;
        this.selectedResolution = selectedResolution;

        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_player_resolution_portrait,
                new FrameLayout(context), false);
        setContentView(binding.getRoot());
        configDialogStyle();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateGrid();
    }

    private void configDialogStyle() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = ScreenUtils.getScreenWidth();
        // 定制高度
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    private void initView() {
        binding.resolutionPlayTitleText.setText("清晰度选择");
        binding.resolutionCancelBtn.setText("取消");
        binding.resolutionCancelBtn.setOnClickListener(v -> dismiss());
    }

    private void updateGrid() {
        binding.resolutionGridlayout.removeAllViews();
        binding.resolutionGridlayout.post(() -> {
            for (int i = 0; i < resolutions.length; i++) {
                String resolution = resolutions[i];
                int row = i / 2;
                int col = i % 2;
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                        GridLayout.spec(row),
                        GridLayout.spec(col)
                );
                int parentWidth = binding.resolutionGridlayout.getWidth();
                lp.width = (parentWidth - SizeUtils.dp2px(40)) / 2;
                lp.height = SizeUtils.dp2px(36);
                lp.leftMargin = lp.rightMargin = SizeUtils.dp2px(10);
                lp.topMargin = SizeUtils.dp2px(16);

                RoundTextView roundTextView = new RoundTextView(getContext());
                roundTextView.setGravity(Gravity.CENTER);
                roundTextView.setTextSize(15);
                roundTextView.setText(ResolutionConst.readableResolutionString(resolution));
                roundTextView.setTag(resolution);
                roundTextView.setRadius(SizeUtils.dp2px(4));
                roundTextView.setOnClickListener(onClickListener);

                binding.resolutionGridlayout.addView(roundTextView, lp);
            }
            updateSelectedResolution();
        });
    }

    private void updateSelectedResolution() {
        for (int i = 0; i < binding.resolutionGridlayout.getChildCount(); i++) {
            RoundTextView child = (RoundTextView) binding.resolutionGridlayout.getChildAt(i);
            String resolution = (String) child.getTag();
            if (StringUtils.equals(resolution, selectedResolution)) {
                child.setBackGroundColor(Color.parseColor("#334086FF"));
                child.setTextColor(Color.parseColor("#4086FF"));
            } else {
                child.setBackGroundColor(Color.parseColor("#2C2D30"));
                child.setTextColor(Color.parseColor("#757577"));
            }
        }
    }

    public void setChangeResolutionListener(Consumer<String> changeResolutionListener) {
        this.changeResolutionListener = changeResolutionListener;
    }
}
