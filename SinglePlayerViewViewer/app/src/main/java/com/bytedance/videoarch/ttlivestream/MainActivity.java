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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bytedance.live.common.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {
    public static String INTENT_ACTIVITY_ID = "activityId";
    public static String INTENT_TOKEN = "token";
    public static String INTENT_IS_PUBLIC = "isPublic";

    private long activityId = 1678089977360392L;
    private String token = "JQCFns";

    EditText idEditText;
    EditText tokenEditText;
    SwitchCompat switchCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idEditText = findViewById(R.id.edittext_activityid);
        tokenEditText = findViewById(R.id.edittext_token);

        idEditText.setText(String.valueOf(activityId));
        tokenEditText.setText(token);

        switchCompat = findViewById(R.id.join_type_switch);
        updateSwitchBtn();
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSwitchBtn();
        });
    }

    private void updateSwitchBtn() {
        switchCompat.setText(switchCompat.isChecked() ? switchCompat.getTextOn() : switchCompat.getTextOff());
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

        //简单通过Token长度判断是否是公开鉴权还是自定义鉴权
        boolean isPublicMode = token.length() <= 6;
        boolean gotoLiveRoomModules = switchCompat.isChecked();
        Class activityClass = gotoLiveRoomModules ? LiveRoomModulesActivity.class : LiveRoomSinglePlayerViewActivity.class;
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(INTENT_ACTIVITY_ID, id);
        intent.putExtra(INTENT_TOKEN, token);
        intent.putExtra(INTENT_IS_PUBLIC, isPublicMode);
        startActivity(intent);
    }

}