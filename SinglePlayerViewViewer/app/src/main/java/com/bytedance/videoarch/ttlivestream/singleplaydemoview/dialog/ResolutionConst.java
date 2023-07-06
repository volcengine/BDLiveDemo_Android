package com.bytedance.videoarch.ttlivestream.singleplaydemoview.dialog;

public class ResolutionConst {
    public static String mCurLD = "流畅";
    public static String mCurSD = "标清";
    public static String mCurHD = "高清";
    public static String mCurFHD = "超清";
    public static String mCurBD = "蓝光";
    public static String mCurOrigin = "原画";
    public static String mCurAuto = "智能";

    public static String readableResolutionString(String resolution) {
        if (resolution != null && !resolution.isEmpty()) {
            try {
                if (resolution.equals("auto")) return mCurAuto;
                if (resolution.equals("origin")) return mCurOrigin;
                if (resolution.equals("ld")) return mCurLD;
                if (resolution.equals("sd")) return mCurSD;
                if (resolution.equals("hd")) return mCurHD;
                if (resolution.equals("uhd")) return mCurFHD;
                if (resolution.equals("bd")) return mCurBD;
                int resolutionValue = Integer.parseInt(resolution.substring(0, resolution.length() - 1));
                if (resolutionValue < 540) return mCurSD;
                if (resolutionValue < 720) return mCurHD;
                if (resolutionValue < 1080) return mCurFHD;
                return mCurBD;
            } catch (Exception ignored) {

            }
        }
        return resolution;
    }
}
