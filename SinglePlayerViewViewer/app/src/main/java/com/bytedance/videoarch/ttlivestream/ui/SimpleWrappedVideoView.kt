package com.bytedance.bdlive.demo.advanced.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.bytedance.bdlive.demo.advanced.data.JoinLiveRoomData
import com.bytedance.live.sdk.player.TVULiveRoom
import com.bytedance.live.sdk.player.TVULiveRoomServer
import com.bytedance.live.sdk.player.listener.SinglePlayerListener
import com.bytedance.live.sdk.player.listener.SinglePlayerListenerAdapter
import com.bytedance.live.sdk.player.model.FusionPlayerModel
import com.bytedance.live.sdk.player.view.tvuSinglePlay.InitConfig
import com.bytedance.live.sdk.player.view.tvuSinglePlay.TVUSinglePlayerView
import com.bytedance.videoarch.ttlivestream.R

class SimpleWrappedVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var roomServer: TVULiveRoomServer? = null;
    var singlePlayerViewContainer: FrameLayout
    var singlePlayerView: TVUSinglePlayerView? = null
    var topImageView: ImageView? = null;
    var playPauseBtn: ImageView? = null;
    var joinLiveRoomData: JoinLiveRoomData? = null
    var playListener: SinglePlayerListener = object : SinglePlayerListenerAdapter() {

        /**
         * playStatus:whether the playerview is playing
         *  0:pause
         *  1:playing
         */
        override fun playStatusChanged(playStatus: Int) {
            playPauseBtn?.setImageResource(
                if (playStatus == 0)
                    R.drawable.tvu_icon_pause_melon
                else R.drawable.tvu_icon_play_melon
            )
        }

        /**
         * playableStatus:the cur available media resource type to play
         * FusionPlayerModel.NONE_PLAYABLE : no media resource to play
         * FusionPlayerModel.VOD_PLAYABLE : vod resource type to play
         * FusionPlayerModel.LIVE_PLAYABLE : live resource type to play
         */
        override fun playableStatusChanged(playableStatus: Int) {
            if (playableStatus == FusionPlayerModel.NONE_PLAYABLE) {
                playPauseBtn?.visibility = GONE
                topImageView?.visibility = VISIBLE
            } else {
                playPauseBtn?.visibility = VISIBLE
                topImageView?.visibility = GONE
            }
        }
    };

    init {
        LayoutInflater.from(getContext()).inflate(
            R.layout.view_simple_wrapped_video_view,
            this, true
        )
        singlePlayerViewContainer = findViewById(R.id.singlePlayerView_container);
        topImageView = findViewById(R.id.top_cover_iv);
        playPauseBtn = findViewById(R.id.play_pause_btn)
        playPauseBtn?.setOnClickListener {
            playOrPause()
        }
    }

    public fun start() {
        val id = joinLiveRoomData!!.id;
        val token = joinLiveRoomData!!.token
        destroy()
        //use a new singlePlayerView to restart liveRoom
        prepareSinglePlayerView()
        val isPublic = true;//all the tokens in demo are public mode token
        //step 1:create TVULiveRoomServer
        roomServer = TVULiveRoomServer(context, id, token, TVULiveRoomServer.SERVER_TYPE_SINGLE)
        roomServer?.roomAuthMode =
            if (isPublic) TVULiveRoom.TVURoomAuthMode.PUBLIC
            else TVULiveRoom.TVURoomAuthMode.CUSTOM

        //step2:init SinglePlayerView
        val initConfig = InitConfig();
        initConfig.singlePlayerListener = playListener
        singlePlayerView?.init(initConfig)
        //custom the video cut mode,value explanation：
        //https://www.volcengine.com/docs/3019/130847#setplayerlayoutmode
        singlePlayerView?.playerLayoutMode = 2
        //set audio mute status
        singlePlayerView?.setMute(false)

        //step3:bind singlePlayerView with roomServer
        roomServer?.playerView = singlePlayerView?.innerPlayerView;
        //step4:start roomServer
        roomServer?.start()
    }

    private fun prepareSinglePlayerView() {
        singlePlayerViewContainer.removeAllViews()
        singlePlayerView = TVUSinglePlayerView(context)
        singlePlayerView?.setOnClickListener {
            if (joinLiveRoomData != null) {
                TVULiveRoom.joinLiveRoom(
                    it.context, joinLiveRoomData!!.id,
                    joinLiveRoomData!!.token,
                    TVULiveRoom.TVURoomAuthMode.PUBLIC, true
                );
            }
        }
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        singlePlayerViewContainer.addView(singlePlayerView, layoutParams)
    }

    fun pauseVideo() {
        singlePlayerView?.pause()
    }

    fun playOrPause() {
        if (singlePlayerView?.isPlaying == true) {
            singlePlayerView?.pause()
        } else {
            singlePlayerView?.play()
        }
    }

    fun destroy() {
        topImageView?.visibility = VISIBLE
        playPauseBtn?.visibility = GONE
        singlePlayerViewContainer.removeAllViews()
        singlePlayerView = null;


        //when not used anymore,call roomServer.closeRoom and the attached singlePlayerView will destroy too
        roomServer?.closeRoom()
        roomServer = null
    }
}