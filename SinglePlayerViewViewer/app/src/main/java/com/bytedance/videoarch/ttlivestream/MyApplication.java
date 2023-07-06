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

package com.bytedance.videoarch.ttlivestream;

import android.app.Application;

import androidx.annotation.NonNull;

import com.bytedance.live.common.env.BDLiveConfig;
import com.bytedance.live.common.env.BDLiveEnv;
import com.pandora.ttlicense2.LicenseManager;


public class MyApplication extends Application {

    public final static String mAppId = "appId";//申请 TTSDK License 时使用的 AppId，请联系技术支持获取。
    public final static String mAppName = "appName";// 申请 TTSDK License 时使用的 AppName，请联系技术支持获取。
    public final static String mRegion = "china";// 申请 TTSDK License 时使用的 region，即 china。
    private final static String mVersion = "1.0.0";//App 版本号
    private final static String mChannel = "channel";// App渠道名
    private final static String mLiveLicenseUri = "assets:///lic/l-101585-ch-live-a-407390.lic";//直播License Uri,如放在assets/lic文件夹，示例：assets:///lic/xxx.lic
    private final static String mVodLicenseUri = "assets:///lic/l-101586-ch-vod-a-407390.lic";//点播License Uri,如放在assets/lic文件夹，示例：assets:///lic/xxx.lic

    @Override
    public void onCreate() {
        super.onCreate();
        initBdLiveEnv();
    }

    private void initBdLiveEnv() {
        //TTSDK 环境初始化
        BDLiveEnv.init(new BDLiveConfig.Builder()
                .setApplicationContext(this)
                .setAppId(mAppId)
                .setAppName(mAppName)
                .setAppChannel(mChannel)
                .setAppVersion(mVersion)
                .setAppRegion(mRegion)
                .setVodLicenseUri(mVodLicenseUri)
                .setLiveLicenseUri(mLiveLicenseUri)
                .setLicenseCallback(new LicenseManager.Callback() {//License 鉴权结果回调，可不填
                    @Override
                    public void onLicenseLoadSuccess(@NonNull String licenseUri, @NonNull String licenseId) {
                        //License 鉴权成功回调
                    }

                    @Override
                    public void onLicenseLoadError(@NonNull String licenseUri, @NonNull Exception e, boolean retryAble) {
                        //License 鉴权失败回调
                    }

                    @Override
                    public void onLicenseLoadRetry(@NonNull String licenseUri) {
                        //License 鉴权重试回调
                    }

                    @Override
                    public void onLicenseUpdateSuccess(@NonNull String licenseUri, @NonNull String licenseId) {
                        //License 更新成功回调
                    }

                    @Override
                    public void onLicenseUpdateError(@NonNull String licenseUri, @NonNull Exception e, boolean retryAble) {
                        //License 更新失败回调
                    }

                    @Override
                    public void onLicenseUpdateRetry(@NonNull String licenseUri) {
                        //License 更新重试回调
                    }
                })
                .build());
    }
}
