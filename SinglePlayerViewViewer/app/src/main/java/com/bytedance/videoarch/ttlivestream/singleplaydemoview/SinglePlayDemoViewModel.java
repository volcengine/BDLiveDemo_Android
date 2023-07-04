package com.bytedance.videoarch.ttlivestream.singleplaydemoview;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bytedance.videoarch.ttlivestream.BR;
import com.bytedance.videoarch.ttlivestream.R;


public class SinglePlayDemoViewModel extends BaseObservable {

    @Bindable
    private boolean controlLayoutVisible = false;

    @Bindable
    private boolean bottomControlBarVisible;

    @Bindable
    private int playIconResId = R.mipmap.tvu_icon_pause_melon;

    @Bindable
    private String curVodPlayTimeText;

    @Bindable
    private String vodDurationText;

    @Bindable
    private int seekBarProgress;

    @Bindable
    private boolean vodPrepared;

    @Bindable
    private boolean livePrepared;


    public boolean isControlLayoutVisible() {
        return controlLayoutVisible;
    }

    public void setControlLayoutVisible(boolean controlLayoutVisible) {
        this.controlLayoutVisible = controlLayoutVisible;
        notifyPropertyChanged(BR.controlLayoutVisible);
    }

    public boolean isBottomControlBarVisible() {
        return bottomControlBarVisible;
    }

    public void setBottomControlBarVisible(boolean bottomControlBarVisible) {
        this.bottomControlBarVisible = bottomControlBarVisible;
        notifyPropertyChanged(BR.bottomControlBarVisible);
    }

    public int getPlayIconResId() {
        return playIconResId;
    }

    public void setPlayIconResId(int playIconResId) {
        this.playIconResId = playIconResId;
        notifyPropertyChanged(BR.playIconResId);
    }

    public String getCurVodPlayTimeText() {
        return curVodPlayTimeText;
    }

    public void setCurVodPlayTimeText(String curVodPlayTimeText) {
        this.curVodPlayTimeText = curVodPlayTimeText;
        notifyPropertyChanged(BR.curVodPlayTimeText);
    }

    public String getVodDurationText() {
        return vodDurationText;
    }

    public void setVodDurationText(String vodDurationText) {
        this.vodDurationText = vodDurationText;
        notifyPropertyChanged(BR.vodDurationText);
    }

    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    public void setSeekBarProgress(int seekBarProgress) {
        this.seekBarProgress = seekBarProgress;
        notifyPropertyChanged(BR.seekBarProgress);
    }

    public boolean isVodPrepared() {
        return vodPrepared;
    }

    public void setVodPrepared(boolean vodPrepared) {
        this.vodPrepared = vodPrepared;
        notifyPropertyChanged(BR.vodPrepared);
    }

    public boolean isLivePrepared() {
        return livePrepared;
    }

    public void setLivePrepared(boolean livePrepared) {
        this.livePrepared = livePrepared;
        notifyPropertyChanged(BR.livePrepared);
    }
}
