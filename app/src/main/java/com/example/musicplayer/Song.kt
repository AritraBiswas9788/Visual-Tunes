package com.example.musicplayer

import android.net.Uri

class Song {
    var title: String? = null
    var songUri:Uri? =null
    var songInfo:String?=null
    var artUri:Uri? =null
    var size:Int?=null
    var duration:Int?=null
    constructor(){}

    constructor( title: String,
                 songUri:Uri,
                 artUri:Uri,
                 songInfo:String,
                 size:Int,
                 duration:Int)
    {
        this.title=title
        this.songInfo=songInfo
        this.songUri=songUri
        this.artUri=artUri
        this.size=size
        this.duration=duration

    }
}

