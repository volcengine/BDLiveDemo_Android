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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.live.common.utils.PermissionUtils;
import com.bytedance.live.common.utils.ToastUtil;
import com.bytedance.live.sdk.player.CustomSettings;
import com.bytedance.live.sdk.player.ServiceApi;
import com.bytedance.live.sdk.player.TVULiveRoom;
import com.bytedance.live.sdk.player.listener.CustomLoginListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final int QR_CODE_SCAN_RESULT = 101;


    static final int MODE_PUBLIC = 1;
    static final int MODE_CUSTOM = 2;

    int curMode;

    String nameSpace;
    String viewUrlPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curMode = MODE_PUBLIC;
        setContentView(R.layout.activity_main);
    }

    public void publicLogin(View view) {
        curMode = MODE_PUBLIC;
        scanQRCode();
    }

    public void customLogin(View view) {
        curMode = MODE_CUSTOM;
        scanQRCode();
    }

    public void scanQRCode() {
        String[] perms = new String[]{Manifest.permission.CAMERA};
        PermissionUtils.permission(perms).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
                startActivityForResult(intent, QR_CODE_SCAN_RESULT);
            }

            @Override
            public void onDenied() {
                displayToast("请检查相机权限");
            }
        }).request();
    }


    public void displayToast(String message) {
        ToastUtil.displayToastCenter(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == QR_CODE_SCAN_RESULT) {
            if (resultCode == RESULT_OK && intent != null) {
                String data = intent.getStringExtra("data");
                Log.d("onActivityResult", data);
                // 进入观看页面
                if (!data.startsWith("http")) {
                    ToastUtil.displayToast("观播地址需要http开头：" + data);
                    return;
                }
                final String[] strings = data.split("/");
                final int[] mode = {curMode};
                nameSpace = strings[strings.length - 2];
                viewUrlPath = strings[strings.length - 1];
                boolean isPortrait = false;
                if (viewUrlPath != null && viewUrlPath.contains("?")) {
                    viewUrlPath = viewUrlPath.substring(0, viewUrlPath.indexOf("?"));
                    isPortrait = true;
                }
                boolean finalIsPortrait = isPortrait;
                new ServiceApi().getRoomMessage(nameSpace, viewUrlPath, mode[0], new ServiceApi.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        try {
                            JSONObject jsonData = new JSONObject(data);
                            JSONObject result = jsonData.optJSONObject("Result");
                            if (result == null) {
                                // 请求房间信息失败
                                displayToast("result is null");
                            } else {
                                String token = result.optString("Token");
                                long activityId = result.optLong("ActivityId");
                                TVULiveRoom.TVURoomAuthMode authMode = mode[0] == 1 ? TVULiveRoom.TVURoomAuthMode.PUBLIC : TVULiveRoom.TVURoomAuthMode.CUSTOM;
                                TVULiveRoom.joinLiveRoom(MainActivity.this, activityId, token, authMode, finalIsPortrait);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("JSONException:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        // 请求房间信息失败
                        displayToast("errCode:" + errCode + "\nerrMsg:" + errMsg);
                    }
                });
            } else {
                displayToast("登录失败，请重试");
            }
        }
    }
}