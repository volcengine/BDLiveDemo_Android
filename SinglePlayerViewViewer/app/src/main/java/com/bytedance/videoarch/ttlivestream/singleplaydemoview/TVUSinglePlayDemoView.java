package com.bytedance.videoarch.ttlivestream.singleplaydemoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.bytedance.live.common.interfaces.Consumer;
import com.bytedance.live.common.utils.StringUtils;
import com.bytedance.live.sdk.player.listener.SinglePlayerListener;
import com.bytedance.live.sdk.player.model.FusionPlayerModel;
import com.bytedance.live.sdk.player.model.vo.generate.PullStreamUrl;
import com.bytedance.live.sdk.player.model.vo.generate.Replay;
import com.bytedance.live.sdk.player.view.PlayerView;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.TVUSinglePlayerView;
import com.bytedance.live.sdk.util.ServerUtil;
import com.bytedance.live.sdk.util.UIUtil;
import com.bytedance.videoarch.ttlivestream.R;
import com.bytedance.videoarch.ttlivestream.databinding.ViewTvuSingleplaydemoBinding;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.FullScreenHelper;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.ResolutionConst;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.TVUResolutionSettingDialog;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.TVUResolutionSettingLandDialog;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.TVUSpeedSettingDialog;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog.TVUSpeedSettingLandDialog;
import com.ss.ttvideoengine.utils.Error;
import com.ss.videoarch.liveplayer.VeLivePlayerError;
import com.ss.videoarch.liveplayer.log.LiveError;

import java.util.Arrays;
import java.util.List;

/**
 * 基于TVUSinglePlayerView包装的一个带控制界面的VideoView，帮助接入TVUSinglePlayerView的客户快速复用
 */
public class TVUSinglePlayDemoView extends FrameLayout implements View.OnClickListener {
    final String TAG = getClass().getSimpleName();
    TVUSinglePlayDemoViewListener demoViewListener;

    SinglePlayDemoViewModel viewModel;
    ViewTvuSingleplaydemoBinding binding;
    TVUSinglePlayerView singlePlayerView;

    int durationTimeInMills;
    int curPlayTimeInMills;

    FullScreenHelper fullScreenHelper;

    Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable autoHideControlLayoutRunnable = () -> {
        if (!singlePlayerView.isPlaying()) {
            return;
        }
        viewModel.setControlLayoutVisible(false);
    };
    boolean isSeeking;
    GestureDetector gestureDetector;
    SeekBar.OnSeekBarChangeListener barChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int seekTime = Math.round((progress * 1f / 100) * durationTimeInMills);
                viewModel.setSeekBarProgress(progress);
                viewModel.setCurVodPlayTimeText(toTimeStr(seekTime));
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
    SinglePlayerListener playerListener = new SinglePlayerListener() {
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
            viewModel.setVodPrepared(false);
            viewModel.setLivePrepared(false);

            if (status == FusionPlayerModel.PlayStatus.LIVE.value || status == FusionPlayerModel.PlayStatus.PLAYBACK.value) {
                binding.switchLineLayout.setVisibility(VISIBLE);
            } else {
                binding.switchLineLayout.setVisibility(GONE);
            }
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

            if (playableStatus == FusionPlayerModel.NONE_PLAYABLE) {
                hideControlLayout();
            } else {
                showControlLayout();
            }

            viewModel.setCurVodPlayTimeText(toTimeStr(0));
            viewModel.setVodDurationText(toTimeStr(0));
            viewModel.setSeekBarProgress(0);
            binding.vodProgressBar.setEnabled(false);
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

            binding.playOrPauseBtn.setImageResource(playStatus == 0 ? R.mipmap.tvu_icon_pause_melon : R.mipmap.tvu_icon_play_melon);
            binding.centerPlayBtn.setVisibility(playStatus == 0 ? VISIBLE : GONE);
            if (playStatus == 0) {
                showControlLayout();
            }
        }

        /**
         * 视频(点播或直播)尺寸方式变化
         * @param width 视频的宽
         * @param height 视频的长
         */
        @Override
        public void sizeChanged(int width, int height) {
            @SuppressLint("DefaultLocale")
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

            if (isStalling) {
                binding.loadingProgressBar.setVisibility(VISIBLE);
                UIUtil.startRotateAnim(binding.loadingProgressBar);
            } else {
                binding.loadingProgressBar.clearAnimation();
                binding.loadingProgressBar.setVisibility(GONE);
            }
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
         * @param veLivePlayerError 错误详情
         */
        @Override
        public void liveErrorOccurred(VeLivePlayerError veLivePlayerError) {
            String msg = "liveErrorOccurred,error:" + veLivePlayerError.mErrorCode;
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

            binding.errorLayout.setVisibility(isPlayError ? VISIBLE : GONE);
            if (isPlayError) {
                hideControlLayout();
            }
        }

        /**
         * 点播视频已准备的回调。这个时候可以做seek相关的操作
         */
        @Override
        public void vodPrepared() {
            String msg = "vodPrepared";
            addLog(msg);

            binding.vodProgressBar.setEnabled(true);
            viewModel.setVodPrepared(true);

            binding.speedSettingBtn.setText("倍速");
        }

        /**
         * 点播视频画面渲染开始回调
         */
        @Override
        public void vodRenderStarted() {
            String msg = "vodRenderStarted";
            addLog(msg);
            showControlLayout();
        }

        @Override
        public void vodAutoSeekPreviousTime(int seekTimeInMills) {
            String msg = "vodAutoSeekPreviousTime,seekTimeInMills: " + seekTimeInMills;
            addLog(msg);
        }


        /**
         * 点播当前视频播放时间改变回调，每秒更新一次
         * @param curTimeInMills 当前播放的位置，单位ms
         */
        @Override
        public void vodCurPlayTimeChanged(int curTimeInMills) {
            String msg = "vodCurPlayTimeChanged,curTimeInMills: " + curTimeInMills;
//            addLog(msg);

            curPlayTimeInMills = curTimeInMills;
            if (!isSeeking) {
                viewModel.setCurVodPlayTimeText(toTimeStr(curTimeInMills));
                viewModel.setSeekBarProgress(Math.round((curTimeInMills * 1f / durationTimeInMills) * 100));
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
            viewModel.setVodDurationText(toTimeStr(durationInMills));
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

            viewModel.setLivePrepared(true);
        }

        /**
         * 直播渲染开始回调
         * @param isFirstFrame 真正渲染的第一帧，因为直播过程中可能会发生重试导致多次触发该回调
         *                     True：当前是调用play之后的第一帧
         *                     False：当前不是调用play之后的第一帧
         */
        @Override
        public void liveFirstFrameRendered(boolean isFirstFrame) {
            String msg = "liveFirstFrameRendered,isFirstFrame: " + isFirstFrame;
            addLog(msg);
            if (isFirstFrame) {
                showControlLayout();
            }
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

            updateResolutionText(ResolutionConst.readableResolutionString(defaultResolution));
        }

        @Override
        public void onCurVodVidChanged(String vid) {
            String msg = String.format("onCurVodVidChanged,vid:%s", vid);
            addLog(msg);
        }

        @Override
        public void onCurReplayListChanged(List<Replay> replayList) {
            String msg = String.format("onCurReplayListChanged,replayList:%s", replayList.toString());
            addLog(msg);
        }

        @Override
        public void onCurLiveLineIdChanged(long lineId) {
            String msg = String.format("onCurLiveLineIdChanged,lineId:%d", lineId);
            addLog(msg);
        }

        @Override
        public void onCurLiveLineListChanged(List<PullStreamUrl> liveLineList) {
            String msg = String.format("onCurLiveLineListChanged,liveLineList:%s", liveLineList.toString());
            addLog(msg);
        }
    };

    public TVUSinglePlayDemoView(@NonNull Context context) {
        this(context, null);
    }

    public TVUSinglePlayDemoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        viewModel = new SinglePlayDemoViewModel();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.view_tvu_singleplaydemo,
                this, true);
        binding.setPlayViewModel(viewModel);
        binding.setClickListener(this);
        binding.vodProgressBar.setOnSeekBarChangeListener(barChangeListener);
        fullScreenHelper = new FullScreenHelper(this);
        singlePlayerView = binding.tvuSingleVideoView;
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(TAG, "onSingleTapConfirmed");
                hideOrShowControlLayout();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap");
                playOrPause();
                return super.onDoubleTap(e);
            }
        });
        binding.rootLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }


    public void initPlayer(InitConfig config) {
        config.singlePlayerListener = playerListener;
        singlePlayerView.init(config);
        singlePlayerView.getInnerPlayerView().setBackground(null);
    }

    public PlayerView getInnerPlayerView() {
        return singlePlayerView.getInnerPlayerView();
    }


    private String toTimeStr(long timeMs) {
        return DateUtils.formatElapsedTime(timeMs / 1000);
    }

    /**
     * 记录日志
     *
     * @param msg 播放器日志
     */
    private void addLog(String msg) {
        Log.d(TAG, msg);
        if (demoViewListener != null) {
            demoViewListener.onLog(msg);
        }
    }

    private void playOrPause() {
        if (singlePlayerView.isPlaying()) {
            singlePlayerView.pause();
        } else {
            singlePlayerView.play();
        }
    }

    private void hideOrShowControlLayout() {
        if (viewModel.isControlLayoutVisible()) {
            hideControlLayout();
        } else {
            showControlLayout();
        }
    }

    private void showControlLayout() {
        int playableStatus = singlePlayerView.getPlayableStatus();
        if (playableStatus == FusionPlayerModel.NONE_PLAYABLE) {
            return;
        }
        mainHandler.removeCallbacks(autoHideControlLayoutRunnable);
        viewModel.setControlLayoutVisible(true);
        mainHandler.postDelayed(autoHideControlLayoutRunnable, 3000);
    }

    private void hideControlLayout() {
        mainHandler.removeCallbacks(autoHideControlLayoutRunnable);
        viewModel.setControlLayoutVisible(false);
    }

    private void updateResolutionText(String curResolution) {
        binding.resolutionSettingBtn.setText(curResolution);
    }

    public boolean consumeBackEvent() {
        if (fullScreenHelper.isFullScreen()) {
            fullScreenHelper.changeFullScreen(!fullScreenHelper.isFullScreen());
            binding.fullScreenBtn.setImageResource(fullScreenHelper.isFullScreen() ?
                    R.mipmap.tvu_small_screen_btn : R.mipmap.tvu_full_screen_btn);
            return true;
        }
        return false;
    }

    private void showSwitchLineDialog() {
        int liveRoomStatus = singlePlayerView.getLiveRoomStatus();
        String[] names = new String[0];
        String[] idList = new String[0];
        int selectedIdx = -1;
        if (liveRoomStatus == FusionPlayerModel.PlayStatus.LIVE.value) {
            List<PullStreamUrl> curLiveLineList = singlePlayerView.getCurLiveLineList();
            long curLiveLineId = singlePlayerView.getCurLiveLineId();
            names = new String[curLiveLineList.size()];
            idList = new String[curLiveLineList.size()];
            for (int i = 0; i < curLiveLineList.size(); i++) {
                PullStreamUrl pullStreamUrl = curLiveLineList.get(i);
                names[i] = pullStreamUrl.getLineName();
                idList[i] = String.valueOf(pullStreamUrl.getLineId());
                if (pullStreamUrl.getLineId() == curLiveLineId) {
                    selectedIdx = i;
                }
            }
        } else if (liveRoomStatus == FusionPlayerModel.PlayStatus.PLAYBACK.value) {
            List<Replay> curReplayList = singlePlayerView.getCurReplayList();
            String curVodVid = singlePlayerView.getCurVodVid();
            names = new String[curReplayList.size()];
            idList = new String[curReplayList.size()];
            for (int i = 0; i < curReplayList.size(); i++) {
                Replay replay = curReplayList.get(i);
                names[i] = replay.getName();
                idList[i] = replay.getVid();
                if (StringUtils.equals(replay.getVid(), curVodVid)) {
                    selectedIdx = i;
                }
            }
        }
        if (names.length <= 0) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择线路");
        String[] finalIdList = idList;
        builder.setSingleChoiceItems(names, selectedIdx, (dialog, which) -> {
            if (liveRoomStatus == FusionPlayerModel.PlayStatus.LIVE.value) {
                singlePlayerView.selectLiveLine(ServerUtil.castStr2Long(finalIdList[which]));
            } else if (liveRoomStatus == FusionPlayerModel.PlayStatus.PLAYBACK.value) {
                singlePlayerView.selectReplay(finalIdList[which]);
            }
            dialog.dismiss();
        });
        builder.create().show();
    }

    public void setDemoViewListener(TVUSinglePlayDemoViewListener demoViewListener) {
        this.demoViewListener = demoViewListener;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.playOrPauseBtn) {
            playOrPause();
        } else if (v == binding.resolutionSettingBtn) {
            Consumer<String> resolutionListener = resolution -> {
                singlePlayerView.setCurResolution(resolution);
                updateResolutionText(ResolutionConst.readableResolutionString(resolution));
            };
            if (fullScreenHelper.isFullScreen()) {
                TVUResolutionSettingLandDialog dialog = new TVUResolutionSettingLandDialog(getContext(),
                        singlePlayerView.getResolutions(), singlePlayerView.getCurResolution());
                dialog.setChangeResolutionListener(resolutionListener);
                dialog.show();
            } else {
                TVUResolutionSettingDialog dialog = new TVUResolutionSettingDialog(getContext(),
                        singlePlayerView.getResolutions(), singlePlayerView.getCurResolution());
                dialog.setChangeResolutionListener(resolutionListener);
                dialog.show();
            }
        } else if (v == binding.speedSettingBtn) {
            if (fullScreenHelper.isFullScreen()) {
                new TVUSpeedSettingLandDialog(getContext(), singlePlayerView.getCurVodPlaySpeed(),
                        singlePlayerView).show();
            } else {
                new TVUSpeedSettingDialog(getContext(), singlePlayerView.getCurVodPlaySpeed(),
                        singlePlayerView).show();
            }
        } else if (v == binding.fullScreenBtn) {
            fullScreenHelper.changeFullScreen(!fullScreenHelper.isFullScreen());
            binding.fullScreenBtn.setImageResource(fullScreenHelper.isFullScreen() ?
                    R.mipmap.tvu_small_screen_btn : R.mipmap.tvu_full_screen_btn);
        } else if (v == binding.errorRetryBtn) {
            singlePlayerView.play();
        } else if (v == binding.centerPlayBtn) {
            singlePlayerView.play();
        } else if (v == binding.switchLineLayout) {
            showSwitchLineDialog();
        } else if (v == binding.refreshLiveBtn) {
            singlePlayerView.refreshLive();
        } else if (v == binding.videoSizeBtn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("视频尺寸");
            String[] sizeNames = new String[]{"适应", "拉伸", "填充"};
            builder.setSingleChoiceItems(sizeNames, singlePlayerView.getPlayerLayoutMode(), (dialog, which) -> {
                singlePlayerView.setPlayerLayoutMode(which);
                dialog.dismiss();
            });
            builder.create().show();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (singlePlayerView.isStalling()) {
            binding.loadingProgressBar.clearAnimation();
            UIUtil.startRotateAnim(binding.loadingProgressBar);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
