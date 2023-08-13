package com.example.musicplayer

import android.content.Context
import android.widget.Toast
import androidx.media3.common.MediaItem

class musicPlayer(
    var contextplayer: Context,
    var songList: ArrayList<Song>,
    var player: androidx.media3.exoplayer.ExoPlayer,
    var playerBar: PlayerBar
): PlayerBar(playerBar) {

    public fun playSong(position: Int, mediaItems: MutableList<MediaItem>, songItem: Song)
    {
        if (!player.isPlaying) {
            player.setMediaItems(mediaItems, position, 0)
        } else {
            player.stop()
            player.clearMediaItems()
            player.setMediaItems(mediaItems,position,0)
            /*player.pause()
            player.seekTo(position, 0)*/

        }
        playerBar.setPlay(true)
        playerBar.updateSongName(songItem.title!!)
        playerBar.updateSongInfo(songItem.songInfo!!)
        player.prepare()
        player.play()
        Toast.makeText(context, "${songItem.title}+${songList[position].title} was clicked", Toast.LENGTH_SHORT).show()
    }



}