package com.bytedance.videoarch.ttlivestream;

import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_ACTIVITY_ID;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_IS_PUBLIC;
import static com.bytedance.videoarch.ttlivestream.MainActivity.INTENT_TOKEN;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.live.common.utils.SizeUtils;
import com.bytedance.live.sdk.player.TVULiveRoom;
import com.bytedance.live.sdk.player.TVULiveRoomServer;
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig;
import com.bytedance.videoarch.ttlivestream.singleplaydemoview.TVUSinglePlayDemoView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LiveRoomSinglePlayerViewActivity extends AppCompatActivity {

    long activityId;
    String token;
    boolean isPublic;

    TVULiveRoomServer roomServer;

    TVUSinglePlayDemoView videoView;

    List<String> logs = new ArrayList<>();
    BaseQuickAdapter<String, BaseViewHolder> adapter;
    RecyclerView logRecyclerView;

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
        roomServer = new TVULiveRoomServer(this, activityId, token, TVULiveRoomServer.SERVER_TYPE_SINGLE);
        roomServer.setRoomAuthMode(isPublic ? TVULiveRoom.TVURoomAuthMode.PUBLIC :
                TVULiveRoom.TVURoomAuthMode.CUSTOM);

        setContentView(R.layout.activity_liveroom_singleplayerview);
        initLogRecyclerView();
        videoView = findViewById(R.id.video_view);

        InitConfig initConfig = new InitConfig();
        videoView.setDemoViewListener(this::addLog);
        videoView.initPlayer(initConfig);

        roomServer.setPlayerView(videoView.getInnerPlayerView());
        roomServer.start();
    }

    private void initLogRecyclerView() {
        logRecyclerView = findViewById(R.id.log_recyclerview);
        Paint paint = new Paint();
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = SizeUtils.dp2px(5);
                }
            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                int childCount = parent.getChildCount();

                for (int i = 0; i < childCount; i++) {
                    View view = parent.getChildAt(i);

                    int index = parent.getChildAdapterPosition(view);
                    //第一个ItemView不需要绘制
                    if (index == 0) {
                        continue;
                    }

                    float dividerTop = view.getTop() - 1;
                    float dividerLeft = parent.getPaddingLeft();
                    float dividerBottom = view.getTop();
                    float dividerRight = parent.getWidth() - parent.getPaddingRight();

                    c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, paint);
                }
            }
        });
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.simple_spinner_item, logs) {
            @Override
            protected void convert(@NonNull BaseViewHolder baseViewHolder, String o) {
                TextView textView = baseViewHolder.itemView.findViewById(android.R.id.text1);
                textView.setSingleLine(false);
                textView.setTextSize(14);
                textView.setText(o);
            }
        };
        logRecyclerView.setAdapter(adapter);
    }

    private void addLog(String log) {
        Date date = new Date();
        String dateStr = new SimpleDateFormat("HH:mm:ss SSS: ", Locale.CHINA).format(date);
        adapter.addData(dateStr + log);
        logRecyclerView.smoothScrollToPosition(logs.size() - 1);
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
