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

package com.bytedance.videoarch.ttLiveStream.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bytedance.videoarch.ttLiveStream.R;

import com.bytedance.videoarch.ttLiveStream.BR;


public class SinglePlayPageModel extends BaseObservable {

    @Bindable
    private boolean controlLayoutVisible;

    @Bindable
    private boolean controlBarVisible;

    @Bindable
    private int playIconResId = R.mipmap.tvu_icon_pause_melon;

    @Bindable
    private String curPlayTimeText;

    @Bindable
    private String durationText;

    @Bindable
    private int seekBarProgress;

    @Bindable
    private boolean vodPrepared;

    @Bindable
    private boolean testLivePrepared;

    @Bindable
    private boolean vodSeekTipVisible;
    @Bindable
    private String vodSeekTipText;


    public boolean isControlLayoutVisible() {
        return controlLayoutVisible;
    }

    public void setControlLayoutVisible(boolean controlLayoutVisible) {
        this.controlLayoutVisible = controlLayoutVisible;
        notifyPropertyChanged(BR.controlLayoutVisible);
    }

    public boolean isControlBarVisible() {
        return controlBarVisible;
    }

    public void setControlBarVisible(boolean controlBarVisible) {
        this.controlBarVisible = controlBarVisible;
        notifyPropertyChanged(BR.controlBarVisible);
    }

    public int getPlayIconResId() {
        return playIconResId;
    }

    public void setPlayIconResId(int playIconResId) {
        this.playIconResId = playIconResId;
        notifyPropertyChanged(BR.playIconResId);
    }

    public String getCurPlayTimeText() {
        return curPlayTimeText;
    }

    public void setCurPlayTimeText(String curPlayTimeText) {
        this.curPlayTimeText = curPlayTimeText;
        notifyPropertyChanged(BR.curPlayTimeText);
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
        notifyPropertyChanged(BR.durationText);
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

    public boolean isTestLivePrepared() {
        return testLivePrepared;
    }

    public void setTestLivePrepared(boolean testLivePrepared) {
        this.testLivePrepared = testLivePrepared;
        notifyPropertyChanged(BR.testLivePrepared);
    }

    public boolean isVodSeekTipVisible() {
        return vodSeekTipVisible;
    }

    public void setVodSeekTipVisible(boolean vodSeekTipVisible) {
        this.vodSeekTipVisible = vodSeekTipVisible;
        notifyPropertyChanged(BR.vodSeekTipVisible);
    }

    public String getVodSeekTipText() {
        return vodSeekTipText;
    }

    public void setVodSeekTipText(String vodSeekTipText) {
        this.vodSeekTipText = vodSeekTipText;
        notifyPropertyChanged(BR.vodSeekTipText);
    }
}
