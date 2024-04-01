package com.bytedance.videoarch.ttlivestream

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.bdlive.demo.advanced.data.LiveRoomConst
import com.bytedance.bdlive.demo.advanced.ui.SimpleWrappedVideoView
import com.bytedance.live.sdk.player.CustomSettings
import com.bytedance.live.sdk.player.TVULiveRoom
import com.bytedance.live.sdk.player.listener.LiveRoomActionListener

class SimpleWrappedDemoActivity : AppCompatActivity() {
    lateinit var videoView1: SimpleWrappedVideoView;
    lateinit var videoView2: SimpleWrappedVideoView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleplayer_demo)
        //don't show floatWindow when exit LiveRoomActivity
        CustomSettings.Holder.mSettings.liveRoomActionListener = object : LiveRoomActionListener {
            override fun onClickExitLiveRoom(activity: Activity?, activityId: Long): Boolean {
                TVULiveRoom.leaveLiveRoom(activity)
                return true
            }
        }
        findViewById<Button>(R.id.start_video1_btn).setOnClickListener {
            playVideo1(it)
        }
        findViewById<Button>(R.id.start_video2_btn).setOnClickListener {
            playVideo2(it)
        }

        videoView1 = findViewById(R.id.video_view_1)
        videoView2 = findViewById(R.id.video_view_2)

        var joinLiveRoomData1 = LiveRoomConst.joinRoomData[0]
        videoView1.joinLiveRoomData = joinLiveRoomData1

        var joinLiveRoomData2 = LiveRoomConst.joinRoomData[1]
        videoView2.joinLiveRoomData = joinLiveRoomData2
    }

    public fun playVideo1(view: View) {
        videoView2.destroy()
        videoView1.start()
    }

    public fun playVideo2(view: View) {
        videoView1.destroy()
        videoView2.start()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        videoView1.pauseVideo()
        videoView2.pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        CustomSettings.Holder.mSettings.liveRoomActionListener = null
        videoView1.destroy()
        videoView2.destroy()
    }
}