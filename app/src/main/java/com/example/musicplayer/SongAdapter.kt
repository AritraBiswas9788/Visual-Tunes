package com.example.musicplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class SongAdapter(
    private var context: Context,
    private var player: musicPlayer
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var playingSongUri: Uri ="".toUri()
    private var playingPosition:Int=-1
    private var lastPos:Int=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view:View =
            LayoutInflater.from(parent.context).inflate(R.layout.songframe, parent, false)
        return SongViewHolder(view)

    }

    override fun getItemCount(): Int {
        return player.songList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val songItem: Song = player.songList[position]
        val viewHolder: SongViewHolder = holder as SongViewHolder
        //Log.i("ArtUri",songItem.title+",/,"+songItem.artUri.toString())
        /*if(songItem.artUri!=null)
            viewHolder.songImg.setImageURI(songItem.artUri)
        else {

            viewHolder.songImg.setImageResource(R.drawable.musicicon)
        }*/
        val placeholder: ArrayList<Int> = arrayListOf(
            R.drawable.placeone,
            R.drawable.planetwo,
            R.drawable.placethree,
            R.drawable.placefour,
            R.drawable.placefive,
            R.drawable.placesix,
            R.drawable.placeseven,
            R.drawable.placeeight,
            R.drawable.placenine,
            R.drawable.placeten
        )
        val ranPlaceHolder = placeholder.random()
        Glide.with(context)
            .load(songItem.artUri)
            .fitCenter()
            .centerCrop()
            //.thumbnail(BaseRequestOptions)
            .thumbnail(0.5f)
            .error(ranPlaceHolder)
            .fallback(ranPlaceHolder)
            .into(viewHolder.songImg)
        viewHolder.songTitle.text = songItem.title
        viewHolder.songInfo.text = songItem.songInfo
        viewHolder.songDuration.text = setCorrectDuration(songItem.duration!!)
        /*if(playingPosition.equals(songItem.songUri.toString()+songItem.title+songItem.songInfo+songItem.size))
            adjustFrame(viewHolder)*/
        viewHolder.itemView.setOnClickListener {
            lastPos=playingPosition
            playingPosition=viewHolder.bindingAdapterPosition
            playingSongUri= songItem.songUri!!
            //val mediaItems=
            /*Toast.makeText(context,player.songList[position].title,Toast.LENGTH_SHORT).show()
            if(songItem.songUri!=player.songList[position].songUri) {
                player.playSong(adjustPositionForQueryState(songItem), getMediaItems(), songItem)
                Toast.makeText(context,"Adjust triggered",Toast.LENGTH_SHORT).show()
            }
            else*/
            player.playSong(position,getMediaItems(),songItem)
            //player.playSong(position,getMediaItems(),songItem)
            if (lastPos!=-1)
                 notifyItemChanged(lastPos)
            notifyItemChanged(playingPosition)
            if(!player.isPlayed) {
                player.playButton.performClick()
                player.playButton.invalidate()
            }
        }
        if ((playingPosition==viewHolder.bindingAdapterPosition&&songItem.songUri==playingSongUri)||songItem.songUri==playingSongUri)
            adjustFrame(viewHolder,true)
        else
            adjustFrame(viewHolder,false)
    }

    private fun adjustPositionForQueryState(songItem: Song): Int {

        for ((position,song) in player.songList.withIndex())
        {
            if(songItem.songUri==song.songUri) {

                Toast.makeText(context,"ADJP: $position",Toast.LENGTH_SHORT).show()
                return position
            }
        }
        Toast.makeText(context,"Item matchup failed",Toast.LENGTH_SHORT).show()
        return 0
    }

    private fun adjustFrame(viewHolder: SongViewHolder, playState: Boolean) {
        if(playState) {
            viewHolder.frameBody.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#002C81")))
            viewHolder.songTitle.setTextColor(Color.WHITE)
            viewHolder.songInfo.setTextColor(Color.parseColor("#D8FFFFFF"))
            viewHolder.moreButton.imageTintList =
                ColorStateList.valueOf(Color.parseColor("#D8FFFFFF"))
            viewHolder.songDuration.setTextColor(Color.parseColor("#D8FFFFFF"))
        }
        else
        {
            viewHolder.frameBody.setCardBackgroundColor(ColorStateList.valueOf(Color.WHITE))
            viewHolder.songTitle.setTextColor(Color.parseColor("#010F39"))
            viewHolder.songInfo.setTextColor(Color.parseColor("#404040"))
            viewHolder.moreButton.imageTintList =
                ColorStateList.valueOf(Color.parseColor("#002C81"))
            viewHolder.songDuration.setTextColor(Color.parseColor("#404040"))
        }

    }

    private fun getMediaItems(): MutableList<MediaItem> {
        val mediaItems = ArrayList<MediaItem>()

        for (song in player.songList) {
            mediaItems.add(
                MediaItem.Builder()
                    .setUri(song.songUri)
                    .setMediaMetadata(getMetaData(song))
                    .build()
            )
        }
        return mediaItems
    }

    private fun getMetaData(song: Song): androidx.media3.common.MediaMetadata {
        return androidx.media3.common.MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtworkUri(song.artUri)
            .setArtist(song.songInfo)
            .build()
    }

    private fun setCorrectDuration(songs_duration: Int): String {
        var songs_duration: String? = songs_duration.toString()
        val time = Integer.valueOf(songs_duration)
        var seconds = time / 1000
        val minutes = seconds / 60
        seconds %= 60
        songs_duration = if (seconds < 10) {
            "$minutes:0$seconds"
        } else {
            "$minutes:$seconds"
        }
        return songs_duration
//        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    public fun filterSongs(filterList: ArrayList<Song>) {
        player.songList = filterList
        notifyDataSetChanged()

    }

    fun reloadRecyclerView() {
        notifyDataSetChanged()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songImg: ImageView = itemView.findViewById(R.id.SongImage)
        var songTitle: TextView = itemView.findViewById(R.id.SongTitle)
        var songInfo: TextView = itemView.findViewById(R.id.SongInfo)
        var songDuration: TextView = itemView.findViewById(R.id.Duration)
        var moreButton: ImageView = itemView.findViewById(R.id.more)
        var frameBody:MaterialCardView=itemView.findViewById(R.id.FrameBody)
    }
}