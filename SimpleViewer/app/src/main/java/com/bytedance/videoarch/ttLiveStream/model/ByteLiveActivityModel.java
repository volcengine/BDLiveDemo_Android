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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ByteLiveActivityModel {
    private String id;
    private String name;
    private String viewUrl;
    private int status;
    private String coverImage;
    private long createTime;
    private long liveTime;
    private int onlineStatus;
    private List<ByteLiveSiteTagModel> siteTagList =null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<ByteLiveSiteTagModel> getSiteTagList() {
        return siteTagList;
    }

    public void setSiteTagList(List<ByteLiveSiteTagModel> siteTagList) {
        this.siteTagList = siteTagList;
    }

    public static ByteLiveActivityModel parseFromJson(JSONObject jsonObj) throws JSONException {
        if (jsonObj == null) {
            return null;
        }
        ByteLiveActivityModel modle = new ByteLiveActivityModel();
        modle.setId(jsonObj.optString("Id"));
        modle.setName(jsonObj.optString("Name"));
        modle.setViewUrl(jsonObj.optString("ViewUrl"));
        modle.setStatus(jsonObj.optInt("Status"));
        modle.setCoverImage(jsonObj.optString("CoverImage"));
        modle.setCreateTime(jsonObj.optLong("CreateTime"));
        modle.setLiveTime(jsonObj.optLong("LiveTime"));
        modle.setOnlineStatus(jsonObj.optInt("OnlineStatus"));
        JSONArray jsonArray = jsonObj.optJSONArray("SiteTags");
        if(jsonArray!=null){
            List<ByteLiveSiteTagModel> siteTagList =new ArrayList<>();
            for(int i = 0;i< jsonArray.length();i++){
                siteTagList.add(ByteLiveSiteTagModel.parseFromJson(jsonArray.optJSONObject(i)));
            }
            modle.setSiteTagList(siteTagList);
        }
        return modle;
    }

    @Override
    public String toString() {
        return "ByteLiveActivityModle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", viewUrl='" + viewUrl + '\'' +
                ", status=" + status +
                ", coverImage='" + coverImage + '\'' +
                ", createTime=" + createTime +
                ", liveTime=" + liveTime +
                ", onlineStatus=" + onlineStatus +
                ", siteTagList=" + siteTagList +
                '}';
    }
}
