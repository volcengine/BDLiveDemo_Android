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

import org.json.JSONObject;

public class ByteLiveSiteTagModel {
    private int index;
    private String value;
    private int dbInsdex;
    private int show;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDbInsdex() {
        return dbInsdex;
    }

    public void setDbInsdex(int dbInsdex) {
        this.dbInsdex = dbInsdex;
    }

    public int isShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ByteLiveSiteTagModle{" +
                "index=" + index +
                ", value='" + value + '\'' +
                ", dbInsdex=" + dbInsdex +
                ", show=" + show +
                ", name='" + name + '\'' +
                '}';
    }

    public static ByteLiveSiteTagModel parseFromJson(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        ByteLiveSiteTagModel modle = new ByteLiveSiteTagModel();
        modle.setIndex(jsonObj.optInt("Index"));
        modle.setValue(jsonObj.optString("Value"));
        modle.setDbInsdex(jsonObj.optInt("DbIndex"));
        modle.setShow(jsonObj.optInt("Show"));
        modle.setName(jsonObj.optString("Name"));
        return modle;
    }
}
