package com.example.musicplayer

import android.content.Context
import android.media.AudioManager
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.exoplayer.ExoPlayer
import com.airbnb.lottie.LottieAnimationView

open class PlayerBar(
     var context: Context,
     var songName: TextView,
     var songInfo: TextView,
     var playButton: LottieAnimationView,
     var likeButton: LottieAnimationView,
     var listenModeButton: ImageView,
     var exoPlayer:ExoPlayer
) {
     var isLiked: Boolean = false
     var isPlayed: Boolean = false

    constructor(playerBar: PlayerBar) : this(
        playerBar.context,
        playerBar.songName,
        playerBar.songInfo,
        playerBar.playButton,
        playerBar.likeButton,
        playerBar.listenModeButton,
        playerBar.exoPlayer
    ) {
    }

    init {
        likeButton.setOnClickListener {
            if (!isLiked) {
                //Toast.makeText(this,"Should pause",Toast.LENGTH_SHORT).show()
                likeButton.setMinAndMaxProgress(0.0f, 0.5f)
                likeButton.playAnimation()
                isLiked = true

            } else {
                //Toast.makeText(this,"Should play",Toast.LENGTH_SHORT).show()
                likeButton.setMinAndMaxProgress(0.5f, 1.0f)
                likeButton.playAnimation()
                isLiked = false

            }
        }
        playButton.setOnClickListener {
            if (!isPlayed) {
                //Toast.makeText(this,"Should pause",Toast.LENGTH_SHORT).show()
                playButton.speed = 1.0f
                playButton.playAnimation()
                isPlayed = true

                exoPlayer.prepare()
                exoPlayer.play()

            } else {
                //Toast.makeText(this,"Should play",Toast.LENGTH_SHORT).show()
                playButton.speed = -1.0f
                playButton.playAnimation()
                isPlayed = false
                exoPlayer.pause()
            }
        }
        listenModeButton.setOnClickListener {
            val audio:AudioManager= context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,AudioManager.FLAG_SHOW_UI)
        }
    }

    public fun updateSongName(name: String) {
        songName.text = name
        songName.isSelected=true
    }

    public fun updateSongInfo(info: String) {
        songInfo.text = info

    }

    public fun setLikeStatus(liked: Boolean) {
        if (liked && !isLiked) {
            likeButton.setMinAndMaxProgress(0.0f, 0.5f)
            likeButton.playAnimation()
            isLiked = true
        } else
            if (!liked && isLiked) {
                likeButton.setMinAndMaxProgress(0.5f, 1.0f)
                likeButton.playAnimation()
                isLiked = false
            }
    }

    public fun setPlay(play: Boolean)
    {
        if(isPlayed && !play)
        {
            playButton.speed = -1.0f
            playButton.playAnimation()
            isPlayed = false
        }
        else if(!isPlayed && play)
        {
            playButton.speed = 1.0f
            playButton.playAnimation()
            isPlayed = true
        }
    }



}
