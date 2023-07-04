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

import com.bytedance.live.common.utils.SizeUtils;
import com.bytedance.live.sdk.player.view.RoundTextView;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.TVUSinglePlayerView;
import com.bytedance.videoarch.ttlivestream.R;
import com.bytedance.videoarch.ttlivestream.databinding.DialogPlayerSpeedLandBinding;

public class TVUSpeedSettingLandDialog extends Dialog implements View.OnClickListener {
    Context context;
    DialogPlayerSpeedLandBinding binding;

    int selectedSpeedIndex;
    TVUSinglePlayerView playerView;

    public TVUSpeedSettingLandDialog(@NonNull Context context, float curSpeed, TVUSinglePlayerView playerView) {
        super(context, R.style.TvuLiveBottomOverlapDialog);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_player_speed_land, new FrameLayout(context),
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
        window.setWindowAnimations(R.style.TVULandDialogAnimation);
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
        binding.speedPlayTitleText.setText("倍速播放");
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
                textView.setTextColor(Color.parseColor("#94C2FF"));
                textView.setBackGroundColor(Color.parseColor("#4D4086FF"));
            } else {
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setBackGroundColor(Color.parseColor("#29FFFFFF"));
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
