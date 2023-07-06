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

import com.bytedance.live.sdk.player.view.RoundTextView;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.TVUSinglePlayerView;
import com.bytedance.videoarch.ttlivestream.R;
import com.bytedance.videoarch.ttlivestream.databinding.DialogPlayerSpeedPortraitBinding;
import com.ss.ttvideoengine.utils.ScreenUtils;

public class TVUSpeedSettingDialog extends Dialog implements View.OnClickListener {
    Context context;
    DialogPlayerSpeedPortraitBinding binding;
    int selectedSpeedIndex;

    TVUSinglePlayerView playerView;

    public TVUSpeedSettingDialog(@NonNull Context context, float curSpeed, TVUSinglePlayerView playerView) {
        super(context, R.style.TvuLiveBottomOverlapDialog);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_player_speed_portrait, new FrameLayout(context),
                false);
        binding.setClickListener(this);
        selectedSpeedIndex = getSpeedIndex(curSpeed);
        this.playerView = playerView;

        setContentView(binding.getRoot());
        configDialogStyle();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSpeedBtn();
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
        binding.speedPlayTitleText.setText("倍速播放");
        binding.speedCancelBtn.setText("取消");
        binding.speedCancelBtn.setOnClickListener(v -> dismiss());
    }

    private int getSpeedIndex(float curSpeed) {
        for (int i = 0; i < SpeedConst.speeds.length; i++) {
            float speed = SpeedConst.speeds[i];
            if (speed == curSpeed) {
                return i;
            }
        }
        return SpeedConst.DEFAULT_SPEED_INDEX;
    }

    private void updateSpeedBtn() {
        updateLinearLayoutBtns(binding.linearlayout1);
        updateLinearLayoutBtns(binding.linearlayout2);
    }

    private void updateLinearLayoutBtns(LinearLayout linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            RoundTextView textView = (RoundTextView) linearLayout.getChildAt(i);
            int speedIndex = Integer.parseInt((String) textView.getTag());
            if (speedIndex == selectedSpeedIndex) {
                textView.setTextColor(Color.parseColor("#3370FF"));
                textView.setBackGroundColor(Color.parseColor("#334086FF"));
            } else {
                textView.setTextColor(Color.parseColor("#757577"));
                textView.setBackGroundColor(Color.parseColor("#2C2D30"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            int speedIndex = Integer.parseInt((String) v.getTag());
            playerView.setVodPlaySpeed(SpeedConst.speeds[speedIndex]);
            selectedSpeedIndex = speedIndex;
            updateSpeedBtn();
        }
    }
}
