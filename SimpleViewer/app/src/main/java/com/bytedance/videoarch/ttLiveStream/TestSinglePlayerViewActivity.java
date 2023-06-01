//
//   AppDelegate.m
//   BDLive
//
//   BDLive SDK License
//
//   Copyright 2022 Beijing Volcano Engine Technology Ltd. All Rights Reserved.
//
//   The BDLive SDK was developed by Beijing Volcanoengine Technology Ltd. (hereinafter “Volcano Engine”).
//   Any copyright or patent right is owned by and proprietary material of the Volcano Engine.
//
//   BDLive SDK is available under the VolcLive product and licensed under the commercial license.
//   Customers can contact service@volcengine.com for commercial licensing options.
//   Here is also a link to subscription services agreement: https://www.volcengine.com/docs/6256/68938.
//
//   Without Volcanoengine's prior written permission, any use of BDLive SDK, in particular any use for commercial purposes, is prohibited.
//   This includes, without limitation, incorporation in a commercial product, use in a commercial service, or production of other artefacts for commercial purposes.
//
//   Without Volcanoengine's prior written permission, the BDLive SDK may not be reproduced, modified and/or made available in any form to any third party.
//

package com.bytedance.videoarch.ttLiveStream;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.live.common.utils.SizeUtils;
import com.bytedance.live.sdk.player.CustomSettings;
import com.bytedance.live.sdk.player.TVULiveRoom;
import com.bytedance.live.sdk.player.listener.SinglePlayerListener;
import com.bytedance.live.sdk.player.model.FusionPlayerModel;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.PlayConfig;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.TVUSinglePlayerView;
import com.bytedance.videoarch.ttLiveStream.databinding.ActivityTestSinglePlayerViewBinding;
import com.bytedance.videoarch.ttLiveStream.model.SinglePlayPageModel;
import com.bytedance.videoarch.ttLiveStream.model.SinglePlayerSettingModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ss.ttvideoengine.utils.Error;
import com.ss.videoarch.liveplayer.log.LiveError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestSinglePlayerViewActivity extends AppCompatActivity {
    TVUSinglePlayerView singlePlayerView;
    String TAG = this.getClass().getSimpleName();

    public static String INTENT_ACTIVITY_ID = "activityId";
    public static String INTENT_TOKEN = "token";
    public static String INTENT_IS_PUBLIC = "isPublic";

    long activityId = 1721285826403358L;
    String token = "tXTIAj";
    boolean isPublic;

    ActivityTestSinglePlayerViewBinding binding;
    SinglePlayPageModel model = new SinglePlayPageModel();
    Handler mainHandler;


    Float[] speeds = new Float[]{0.5f, 1f, 1.5f, 2f};

    String[] layoutModes = new String[]{"AspectFit", "Fill", "AspectFill"};


    int curPlayTimeInMills;
    int durationTimeInMills;
    boolean isSeeking;
    SeekBar.OnSeekBarChangeListener barChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int seekTime = Math.round((progress * 1f / 100) * durationTimeInMills);
                model.setSeekBarProgress(progress);
                model.setCurPlayTimeText(toTimeStr(seekTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeeking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeeking = false;
            int seekTime = Math.round((seekBar.getProgress() * 1f / 100) * durationTimeInMills);
            singlePlayerView.seekVodTime(seekTime, null);
        }
    };

    List<String> logs = new ArrayList<>();
    BaseQuickAdapter adapter;
    Paint paint = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //一进来先销毁当前浮窗
        TVULiveRoom.destroyFloatWindow();
        mainHandler = new Handler(Looper.getMainLooper());
        activityId = getIntent().getLongExtra(INTENT_ACTIVITY_ID, 0);
        token = getIntent().getStringExtra(INTENT_TOKEN);
        isPublic = getIntent().getBooleanExtra(INTENT_IS_PUBLIC, true);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_single_player_view);
        binding.setPageModel(model);
        binding.seekbar.setOnSeekBarChangeListener(barChangeListener);

        singlePlayerView = findViewById(R.id.singlePlayerView);
        SinglePlayerListener singlePlayerListener = new SinglePlayerListener() {
            /**
             * singlePlayerView初始化结果的回调
             * @param initSuccess 初始化是否成功，如果为false需要重新init
             */
            @Override
            public void initFinished(boolean initSuccess) {
                String msg = "initFinished,initSuccess: " + initSuccess;
                addLog(msg);
            }

            /**
             * 直播间状态切换回调
             * @param status 直播间状态
             *               1：直播中
             *               2：预告
             *               3：回放
             *               4：已结束
             */
            @Override
            public void liveRoomStatusChanged(int status) {
                String msg = "liveRoomStatusChanged,status: " + status;
                addLog(msg);
                model.setVodPrepared(false);
                model.setTestLivePrepared(false);
            }

            /**
             * 媒体资源可播放回调，此时可以调用play进行播放
             * @param playableStatus 可播放资源状态
             *                       0：当前无任何资源可播放，无法调用play
             *                       1：当前有点播视频(包含预告和回放视频)可播放
             *                       2：当前有直播可播放
             */
            @Override
            public void playableStatusChanged(int playableStatus) {
                String msg = "playableStatusChanged,playableStatus: " + playableStatus;
                addLog(msg);

                model.setControlLayoutVisible(playableStatus != FusionPlayerModel.NONE_PLAYABLE);
                model.setControlBarVisible(playableStatus == FusionPlayerModel.VOD_PLAYABLE);

                model.setCurPlayTimeText(toTimeStr(0));
                model.setDurationText(toTimeStr(0));
                model.setSeekBarProgress(0);
                binding.seekbar.setEnabled(false);
            }

            /**
             * 播放状态改变回调
             * @param playStatus 当前的播放状态
             *                   0：暂停中
             *                   1：播放中
             */
            @Override
            public void playStatusChanged(int playStatus) {
                String msg = "playStatusChanged,playStatus: " + playStatus;
                addLog(msg);

                model.setPlayIconResId(playStatus == 0 ? R.mipmap.tvu_icon_pause_melon : R.mipmap.tvu_icon_play_melon);
            }

            /**
             * 视频(点播或直播)尺寸方式变化
             * @param width 视频的宽
             * @param height 视频的长
             */
            @Override
            public void sizeChanged(int width, int height) {
                String msg = String.format("sizeChanged,width:%d,height:%d", width, height);
                addLog(msg);
            }

            /**
             * 当前卡顿状态发生变化，此时可以显示或隐藏自己的loading动画
             * @param isStalling 当前是否卡顿
             *                   True：卡顿
             *                   False：不卡顿
             */
            @Override
            public void stallingStatusChanged(boolean isStalling) {
                String msg = "stallingStatusChanged,isStalling: " + isStalling;
                addLog(msg);

                binding.loadingProgressLayout.setVisibility(isStalling ? View.VISIBLE : View.GONE);
            }

            /**
             * 点播发生错误的回调
             * @param error 错误详情
             */
            @Override
            public void vodErrorOccurred(Error error) {
                String msg = "vodErrorOccurred,error:" + error.toString();
                addLog(msg);
            }

            /**
             * 直播发生错误的回调
             * @param error 错误详情
             */
            @Override
            public void liveErrorOccurred(LiveError error) {
                String msg = "liveErrorOccurred,error:" + error.toString();
                addLog(msg);
            }

            /**
             * 对播放中vodErrorOccurred和liveErrorOccurred回调以及其他播放错误的汇总回调，此时播放器会处于暂停状态，开发者可以展示重试画面
             * 引导用户点击重试播放
             * @param isPlayError 当前是否发生播放错误
             *                    True：发生错误
             *                    False：没有播放错误
             */
            @Override
            public void playErrorStatusChanged(boolean isPlayError) {
                String msg = "playErrorStatusChanged,isPlayError: " + isPlayError;
                addLog(msg);
            }

            /**
             * 点播视频已准备的回调。这个时候可以做seek相关的操作
             */
            @Override
            public void vodPrepared() {
                String msg = "vodPrepared";
                addLog(msg);

                binding.seekbar.setEnabled(true);
                model.setVodPrepared(true);

                binding.spinnerSpeed.setSelection(1);
            }

            /**
             * 点播视频画面渲染开始回调
             */
            @Override
            public void vodRenderStarted() {
                String msg = "vodRenderStarted";
                addLog(msg);
            }

            @Override
            public void vodAutoSeekPreviousTime(int seekTimeInMills) {
                model.setVodSeekTipText("已为您定位至 " + DateUtils.formatElapsedTime(seekTimeInMills / 1000));
                model.setVodSeekTipVisible(true);
                mainHandler.postDelayed(() -> model.setVodSeekTipVisible(false), 2000);
            }


            /**
             * 点播当前视频播放时间改变回调，每秒更新一次
             * @param curTimeInMills 当前播放的位置，单位ms
             */
            @Override
            public void vodCurPlayTimeChanged(int curTimeInMills) {
                curPlayTimeInMills = curTimeInMills;
                if (!isSeeking) {
                    model.setCurPlayTimeText(toTimeStr(curTimeInMills));
                    model.setSeekBarProgress(Math.round((curTimeInMills * 1f / durationTimeInMills) * 100));
                }
            }

            /**
             * 点播视频时长变化回调，通常发生在视频切换的时候
             * @param durationInMills 当前视频的总时长，单位ms
             */
            @Override
            public void vodDurationChanged(int durationInMills) {
                String msg = "vodDurationChanged,durationInMills: " + durationInMills;
                addLog(msg);

                durationTimeInMills = durationInMills;
                model.setDurationText(toTimeStr(durationInMills));
            }

            /**
             * 视频播放完成回调
             */
            @Override
            public void vodCompletion() {
                String msg = "vodCompletion";
                addLog(msg);
            }

            /**
             * 直播已准备的回调
             */
            @Override
            public void livePrepared() {
                String msg = "livePrepared";
                addLog(msg);

                model.setTestLivePrepared(true);
            }

            /**
             * 直播渲染开始回调
             * @param isFirstFrame 真正渲染的第一帧，因为直播过程中可能会发生重试导致多次触发该回调
             *                     True：当前是调用play之后的第一帧
             *                     Flase：当前不是调用play之后的第一帧
             */
            @Override
            public void liveFirstFrameRendered(boolean isFirstFrame) {
                String msg = "liveFirstFrameRendered,isFirstFrame: " + isFirstFrame;
                addLog(msg);
            }

            /**
             * 直播结束回调
             */
            @Override
            public void liveCompletion() {
                String msg = "liveCompletion";
                addLog(msg);
            }

            /**
             * 封面图显示隐藏回调，当视频开始播放时封面会隐藏，无视频资源可以播放时封面会显示
             * @param isVisible 当前可见状态
             *                  True：显示
             *                  False：隐藏
             */
            @Override
            public void coverImageVisibleChanged(boolean isVisible) {
                String msg = "coverImageVisibleChanged,isVisible: " + isVisible;
                addLog(msg);
            }

            /**
             * 视频(点播或直播)分辨率信息改变回调。通常发生在直播和点播互相切换的时候
             * @param resolutions 当前支持的分辨率列表
             * @param defaultResolution 当前默认选中的分辨率
             */
            @Override
            public void resolutionInfoChanged(String[] resolutions, String defaultResolution) {
                String msg = String.format("resolutionInfoChanged,resolutions:%s\ndefaultResolution:%s", Arrays.toString(resolutions), defaultResolution);
                addLog(msg);

                binding.spinnerResolution.setAdapter(new ArrayAdapter<>(
                        TestSinglePlayerViewActivity.this
                        , android.R.layout.simple_spinner_item,
                        resolutions
                ));
                int i = Arrays.binarySearch(resolutions, defaultResolution);
                binding.spinnerResolution.setSelection(i);
                binding.spinnerResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("tttttttt", "onItemSelected: " + position);
                        singlePlayerView.setCurResolution(resolutions[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        };
        InitConfig initConfig = new InitConfig(
                activityId,
                token,
                isPublic ? TVULiveRoom.TVURoomAuthMode.PUBLIC :
                        TVULiveRoom.TVURoomAuthMode.CUSTOM,
                singlePlayerListener
        );
        SinglePlayerSettingModel model = SinglePlayerSettingModel.Holder.mModel;
        initConfig.setPlayConfig(new PlayConfig(
                toBoolean(model.singlePlayerLiveAutoPlay),
                toBoolean(model.singlePlayerForeShowAutoPlay),
                toBoolean(model.singlePlayerPlayBackAutoPlay),
                toBoolean(model.singlePlayerForeShowLoop),
                toBoolean(model.singlePlayerPlayBackLoop)));
        singlePlayerView.init(initConfig);
        binding.spinnerSpeed.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, speeds));
        binding.spinnerSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                singlePlayerView.setVodPlaySpeed(speeds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paint.setColor(Color.BLACK);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    //这里直接硬编码为1px
                    outRect.top = SizeUtils.dp2px(5);
                }
            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                int childCount = parent.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View view = parent.getChildAt(i);

                    int index = parent.getChildAdapterPosition(view);
                    //第一个ItemView不需要绘制
                    if (index == 0) {
                        continue;
                    }

                    float dividerTop = view.getTop() - 1;
                    float dividerLeft = parent.getPaddingLeft();
                    float dividerBottom = view.getTop();
                    float dividerRight = parent.getWidth() - parent.getPaddingRight();

                    c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, paint);
                }
            }
        });
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.simple_spinner_item, logs) {
            @Override
            protected void convert(@NonNull BaseViewHolder baseViewHolder, String o) {
                TextView textView = baseViewHolder.itemView.findViewById(android.R.id.text1);
                textView.setSingleLine(false);
                textView.setTextSize(14);
                textView.setText(o);
            }
        };
        binding.recyclerview.setAdapter(adapter);

        binding.spinnerLayoutMode.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                layoutModes));
        binding.spinnerLayoutMode.setSelection(CustomSettings.Holder.mSettings.getPlayerLayoutMode());
        binding.spinnerLayoutMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                singlePlayerView.setPlayerLayoutMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // loading 动画
        Animation operatingAnim = AnimationUtils.loadAnimation(this, com.bytedance.live.sdk.R.anim.tvu_loading_repeat);
        operatingAnim.setRepeatCount(Animation.INFINITE);
        operatingAnim.setInterpolator(new LinearInterpolator());
        binding.loadingProgressBar.startAnimation(operatingAnim);
    }

    @Override
    protected void onPause() {
        super.onPause();
        singlePlayerView.pause();
        singlePlayerView.setInBackground(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        singlePlayerView.setInBackground(false);
        if (singlePlayerView.getPlayableStatus() != FusionPlayerModel.NONE_PLAYABLE) {
            singlePlayerView.play();
        }
    }

    public void playOrPause(View view) {
        if (singlePlayerView.isPlaying()) {
            pause(view);
        } else {
            play(view);
        }
    }

    public void play(View view) {
        singlePlayerView.play();
    }

    public void pause(View view) {
        singlePlayerView.pause();
    }

    public void seekWith10Seconds(View view) {
        int i = curPlayTimeInMills + 10 * 1000;
        Log.d("tttttt2", i + "");
        singlePlayerView.seekVodTime(i, null);
    }

    private void addLog(String log) {
        Log.d(TAG, log);
        mainHandler.post(() -> {
            Date date = new Date();
            String dateStr = new SimpleDateFormat("HH:mm:ss SSS: ", Locale.CHINA).format(date);
            adapter.addData(dateStr + log);
            binding.recyclerview.smoothScrollToPosition(logs.size() - 1);
        });
    }


    private String toTimeStr(long timeMs) {
        return DateUtils.formatElapsedTime(timeMs / 1000);
    }

    private boolean toBoolean(int n) {
        return n > 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        singlePlayerView.destroy();
    }
}