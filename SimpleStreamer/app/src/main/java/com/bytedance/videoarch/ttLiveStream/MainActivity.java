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

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.live.common.utils.ToastUtil;
import com.bytedance.live.push.TVUPushLiveRoom;
import com.bytedance.live.push.logic.settings.CVSettings;
import com.bytedance.live.push.logic.settings.PushSettings;

public class MainActivity extends AppCompatActivity {

    private final String activityId = "1795897321751579";//fill activity id,contact Technical Support to get
    private final String secretKey = "ByIHznZVF/CnCffhWFEfEfxXFsGcyBUdfJvyrtkVLJv6r3lxPrRVlO9vSntUY7QG";//fill activity secretKey,contact Technical Support to get

    EditText idEditText;
    EditText tokenEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idEditText = findViewById(R.id.edittext_activityid);
        tokenEditText = findViewById(R.id.edittext_token);

        idEditText.setText(activityId);
        tokenEditText.setText(secretKey);
//        openCV();
    }


    public void goLiveRoom(View view) {
        long id;
        try {
            id = Long.parseLong(idEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            ToastUtil.displayToast(e.getMessage());
            return;
        }
        String token = tokenEditText.getText().toString().trim();

        TVUPushLiveRoom.startPushLiveRoom(this, id, token);
    }

    private void openCV(){
        CVSettings cvSettings = PushSettings.Holder.mSettings.getCvSettings();
        //开启CV
        cvSettings.setEnableCV(true);

        //设置CV资源包对应版本号，版本号变大后SDK会重新解压CV资源包，建议每次更新CV资源包后版本号+1
        cvSettings.setResourceVersionCode(1);

        //设置CV资源包在Android assets目录中的路径
        cvSettings.setResourceAssetName("resource.zip");

        //设置CV在线授权的LicenseKey和LicenseSecret
        cvSettings.setOnLineLicenseInfo("XXX",
                "XXX");
    }

}