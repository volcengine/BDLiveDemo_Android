package com.bytedance.videoarch.ttlivestream;

import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_ACTIVITY_ID;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_IS_PUBLIC;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_TOKEN;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.live.sdk.player.TVULiveRoom;
import com.bytedance.live.sdk.player.TVULiveRoomServer;
import com.bytedance.live.sdk.player.listener.ITVULiveRoomServerListener;
import com.bytedance.live.sdk.player.model.vo.generate.ActivityResult;
import com.bytedance.live.sdk.player.view.TVUCommentListView;
import com.bytedance.live.sdk.player.view.TVUPeopleHotCountView;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.TVUSinglePlayDemoView;

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
