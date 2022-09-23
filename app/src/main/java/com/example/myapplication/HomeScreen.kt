package com.example.myapplication

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class HomeScreen : AppCompatActivity() {
    companion object{
        var isFullScreen = false
        var isLock = false
    }
    lateinit var handler: Handler
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var bt_fullscreen: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        handler = Handler(Looper.getMainLooper())
        val playerView = findViewById<PlayerView>(R.id.player)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        bt_fullscreen =  findViewById<ImageView>(R.id.bt_fullscreen)
        val bt_lockscreen = findViewById<ImageView>(R.id.exo_lock)

        bt_fullscreen.setOnClickListener {

            if(!isFullScreen)
            {
                bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen_exit))
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            }
            else{
                bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_fullscreen))
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
            isFullScreen = !isFullScreen
        }

        bt_lockscreen.setOnClickListener {
            if(!isLock)
            {
                bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_lock))
            }
            else
            {
                bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_lock_open))
            }
            isLock = !isLock
            lockScreen(isLock)
        }


        simpleExoPlayer= SimpleExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        playerView.player = simpleExoPlayer
        playerView.keepScreenOn = true
        simpleExoPlayer.addListener(object: Player.Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int)
            {
                if(playbackState == Player.STATE_BUFFERING)
                {
                    progressBar.visibility = View.VISIBLE
                }
                else if(playbackState == Player.STATE_READY)
                {
                    progressBar.visibility = View.GONE
                }

                if(!simpleExoPlayer.playWhenReady)
                {
                    handler.removeCallbacks(updateProgressAction)
                }
                else
                {
                    onProgress()
                }
            }
        })
        val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
        val mediaItem = MediaItem.fromUri(videoSource)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }
    //at 4 second
    val ad = 4000
    var check = false
    fun onProgress()
    {
        val player= simpleExoPlayer
        val position :Long =if(player == null) 0 else player.currentPosition
        handler.removeCallbacks(updateProgressAction)
        val playbackState = if(player ==null) Player.STATE_IDLE else player.playbackState
        if(playbackState != Player.STATE_IDLE && playbackState!= Player.STATE_ENDED)
        {
            var delayMs :Long
            if(player.playWhenReady && playbackState == Player.STATE_READY)
            {
                delayMs  = 1000 - position % 1000
                if(delayMs < 200)
                {
                    delayMs+=1000
                }
            }
            else{
                delayMs = 1000
            }

            //check to display ad
            if((ad-3000 <= position && position<=ad) &&!check)
            {
                check =true
              //  initAd()
            }
            handler.postDelayed(updateProgressAction,delayMs)
        }
    }

    /*var rewardedInterstitialAd: RewardedInterstitialAd ?=null
    private fun initAd()
    {
        if(rewardedInterstitialAd!=null) return
        MobileAds.initialize(this)
        RewardedInterstitialAd.load(this,"ca-app-pub-3940256099942544/5354046379",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback(){
                override fun onAdLoaded(p0: RewardedInterstitialAd) {
                    rewardedInterstitialAd =p0
                    rewardedInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback()
                    {
                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            Log.d("WatchActivity_AD",p0?.message.toString())
                        }

                        override fun onAdShowedFullScreenContent() {
                            handler.removeCallbacks(updateProgressAction)
                        }

                        override fun onAdDismissedFullScreenContent() {
                            //resume play
                            simpleExoPlayer.playWhenReady = true
                            rewardedInterstitialAd = null
                            check=false
                        }
                    }
                    val sec_ad_countdown = findViewById<LinearLayout>(R.id.sect_ad_countdown)
                    val tx_ad_countdown = findViewById<TextView>(R.id.tx_ad_countdown)
                    sec_ad_countdown.visibility = View.VISIBLE
                    object : CountDownTimer(4000,1000)
                    {
                        override fun onTick(p0: Long) {
                            tx_ad_countdown.text = "Ad in ${p0/1000}"
                        }

                        override fun onFinish() {
                            sec_ad_countdown.visibility = View.GONE
                            rewardedInterstitialAd!!.show(this@WatchActivity, object : OnUserEarnedRewardListener{
                                override fun onUserEarnedReward(p0: RewardItem) {

                                }

                            })
                        }

                    }.start()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    rewardedInterstitialAd = null
                }


            })
    }*/

    private val updateProgressAction = Runnable { onProgress() }
    private fun lockScreen(lock: Boolean) {
        val sec_mid = findViewById<LinearLayout>(R.id.sec_controlvid1)
        val sec_bottom = findViewById<LinearLayout>(R.id.sec_controlvid2)
        if(lock)
        {
            sec_mid.visibility = View.INVISIBLE
            sec_bottom.visibility = View.INVISIBLE
        }
        else
        {
            sec_mid.visibility = View.VISIBLE
            sec_bottom.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if(isLock) return
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            bt_fullscreen.performClick()
        }
        else super.onBackPressed()

    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
    }
}