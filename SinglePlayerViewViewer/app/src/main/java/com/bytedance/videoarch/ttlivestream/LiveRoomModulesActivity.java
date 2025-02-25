package com.bytedance.videoarch.ttlivestream;

import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_ACTIVITY_ID;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_IS_PUBLIC;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_TOKEN;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.live.common.utils.ToastUtil;
import com.bytedance.live.common.ws.WSCustomIMListener;
import com.bytedance.live.sdk.player.CustomSettings;
import com.bytedance.live.sdk.player.ServiceApi;
import com.bytedance.live.sdk.player.TVULiveRoom;
import com.bytedance.live.sdk.player.TVULiveRoomServer;
import com.bytedance.live.sdk.player.listener.ITVULiveRoomServerListener;
import com.bytedance.live.sdk.player.listener.RedirectPageListener;
import com.bytedance.live.sdk.player.logic.thumb.ThumbFlowingContainer;
import com.bytedance.live.sdk.player.model.vo.generate.ActivityResult;
import com.bytedance.live.sdk.player.model.vo.response.SendCommentResponse;
import com.bytedance.live.sdk.player.view.TVUCommentListView;
import com.bytedance.live.sdk.player.view.TVUPeopleHotCountView;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.TVUSinglePlayDemoView;

import java.util.Random;

public class LiveRoomModulesActivity extends AppCompatActivity {



    long activityId;
    String token;
    boolean isPublic;

    TVULiveRoomServer roomServer;

    TVUSinglePlayDemoView videoView;
    TVUPeopleHotCountView peopleHotCountView;
    TVUCommentListView tvuCommentListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityId = getIntent().getLongExtra(INTENT_ACTIVITY_ID, 0);
        token = getIntent().getStringExtra(INTENT_TOKEN);
        isPublic = getIntent().getBooleanExtra(INTENT_IS_PUBLIC, true);

        /**
         * SERVER_TYPE_LIVEROOM:直播间类型，如果需要绑定直播间的其它组件，设置成这个类型
         * SERVER_TYPE_SINGLE:独立播放器类型，不绑定其它组件，设置成这个类型
         */
        //确保new TVULiveRoomServer在setContentView之前执行
        roomServer = new TVULiveRoomServer(this, activityId, token, TVULiveRoomServer.SERVER_TYPE_LIVEROOM);
        roomServer.setRoomAuthMode(isPublic ? TVULiveRoom.TVURoomAuthMode.PUBLIC :
                TVULiveRoom.TVURoomAuthMode.CUSTOM);

        setContentView(R.layout.activity_liveroom_modules);
        videoView = findViewById(R.id.video_view);
        peopleHotCountView = findViewById(R.id.people_hot_count_view);
        tvuCommentListView = findViewById(R.id.comment_list_view);

        InitConfig initConfig = new InitConfig();
        videoView.initPlayer(initConfig);
        roomServer.setPlayerView(videoView.getInnerPlayerView());
        roomServer.setListener(new ITVULiveRoomServerListener() {
            @Override
            public void onGetRoomDataSuccess(ActivityResult activityResult) {
                //进房成功
                peopleHotCountView.attachRoomServer(roomServer);
                tvuCommentListView.attachRoomServer(roomServer);
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onGetRoomDataFailed(int errCode, String errMsg) {
                //进房失败
            }

            @Override
            public void liveRoomStatusChange(int value) {

            }

            @Override
            public void playerStatusChange(int playerStatus) {

            }
        });

        //隐藏底部输入框
        tvuCommentListView.setShowCommentBottomBar(false);

        //监听直播间内调用OPENAPI发送的自定义IM消息
        roomServer.getWsConnector().addListener(new WSCustomIMListener() {
            @Override
            public void onConnectFailed(String s, String s1) {

            }

            @Override
            public void onConnected(long l) {

            }

            @Override
            public void onReceiveIMString(String str) {
                ToastUtil.displayToast("onReceiveIMStr:"+str);
            }

        });
        findViewById(R.id.send_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("[ByDesign7.4]WeakPRNG")
                String s = String.valueOf(new Random(System.currentTimeMillis()).nextInt());
                //发送文本消息
                roomServer.getCommentDataManager().sendTextMessage(s, new ServiceApi.ResultCallback<SendCommentResponse>() {
                    @Override
                    public void onSuccess(SendCommentResponse data) {

                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        if (errCode == ServiceApi.FREQUENCY_LIMIT){
                            //发送消息有频率限制，需要处理
                            ToastUtil.displayToast("频率限制");
                        }
                    }
                });
            }
        });
        findViewById(R.id.send_thumb_anim_btn).setOnClickListener(v -> {
            //抽离出的点赞动画的视图，也可以自己实现，参考ThumbFlowingContainer内部的startAnimation和createPath方法
            ThumbFlowingContainer flowingContainer = findViewById(R.id.thumb_anim_layout);
            flowingContainer.startAnimation(v.getContext());
        });
        CustomSettings.Holder.mSettings.setRedirectPageListener((liveActivity, redirectInfo) -> {
            if (redirectInfo.getEntrance().equals(RedirectPageListener.Entrance.COMMENT_CONTENT.value)){
                //自定义拦截评论内容的点击，不显示回复评论的弹框
                ToastUtil.displayToast("Click: "+redirectInfo.getCommentModel().getContent());
                return true;
            }
            return false;
        });

        roomServer.start();
    }

    @Override
    public void onBackPressed() {
        if (videoView.consumeBackEvent()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomServer.closeRoom();
    }
}
