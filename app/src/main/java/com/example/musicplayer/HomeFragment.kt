package com.example.musicplayer

import android.app.Activity
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.textfield.TextInputEditText
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchBar: TextInputEditText
    private lateinit var searchButton: Button
    private lateinit var sortButton: Button
    private lateinit var shuffleButton: LottieAnimationView
    private lateinit var recView:RecyclerView
    private lateinit var queryFrame:TextView
    private var songRecList=ArrayList<Song>()
    private lateinit var songAdapter: SongAdapter
    private lateinit var musicPlayer: musicPlayer
    private var checkedItemId:Int = 0
    private var sortId="Title"
    private var queryActive = false
    //var width=0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortButton=view.findViewById(R.id.sortButton)
        searchBar=view.findViewById(R.id.searchBar)
        searchButton=view.findViewById(R.id.SearchIcon)
        shuffleButton=view.findViewById(R.id.shuffleButton)
        recView=view.findViewById(R.id.SongRecView)
        queryFrame=view.findViewById(R.id.QueryFrame)
        queryFrame.visibility=View.GONE
        val exoPlayer=(activity as HomeActivity).sendMainAudioPlayer()
        musicPlayer= musicPlayer(requireContext(),fetchSongs(""),exoPlayer,(activity as HomeActivity).sendPlayerBar())
        /*searchBar.doOnLayout {
            width= it.width.toFloat()
        }*/

        showSongs(fetchSongs(""))

        //storagePermissionLauncher.launch(permission)
        searchBar.visibility=View.GONE


        shuffleButton.setMinAndMaxProgress(0.1f,0.75f)
        shuffleButton.setOnClickListener {
            shuffleButton.playAnimation()
        }
        sortButton.setOnClickListener {
            val popupMenu=PopupMenu(requireContext(),sortButton)
            popupMenu.menuInflater.inflate(R.menu.sortmenu,popupMenu.menu)
            if (checkedItemId!=0)
                popupMenu.menu.findItem(checkedItemId).isChecked=true

            popupMenu.setOnMenuItemClickListener {item ->
                item.isChecked = true
                checkedItemId=item.itemId
                sortId=item.title.toString()

                musicPlayer.songList=fetchSongs("")
                //showSongs(fetchSongs(""))
                songAdapter.reloadRecyclerView()
                //Toast.makeText(context, "Clicked:${item.title}",Toast.LENGTH_SHORT).show()
                true
            }
            popupMenu.show()
        }

        searchButton.setOnClickListener {
            if(searchBar.visibility==View.GONE)
            {
                sortButton.visibility=View.GONE
                searchBar.visibility=View.VISIBLE

                searchBar.doOnLayout {
                    val width=searchBar.width
                    searchBar.requestFocus()
                    //Toast.makeText(context, "Val retrieved:$width",Toast.LENGTH_SHORT).show()
                    searchButton.animate().xBy(-width.toFloat())
                    (activity as HomeActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    val imm: InputMethodManager =
                        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchBar, 0)

                }
            }
            else
            {
                val width=searchBar.width
                val queryStr=searchBar.text.toString()+"\t \t"

                if(queryActive && !TextUtils.isEmpty(searchBar.text))
                {
                    queryFrame.text=queryStr
                    queryFrame.visibility=View.VISIBLE
                    val dp:Float = 37.0f
                    val px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        resources.displayMetrics
                    )
                    val params=recView.layoutParams as ViewGroup.MarginLayoutParams
                    //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayoutRv.getLayoutParams();
                    params.setMargins(0,px.toInt(),0,0 )
                    recView.layoutParams = params

                }
                searchBar.setText("")
                songAdapter.filterSongs(fetchSongs(queryStr.trim()))
                sortButton.visibility=View.VISIBLE
                searchBar.visibility=View.GONE
                searchButton.animate().xBy(width.toFloat())
                val imm: InputMethodManager =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken,0)


            }
        }

        queryFrame.setOnClickListener {
            if(queryActive)
            {
                queryActive=false
                queryFrame.visibility=View.GONE
                songAdapter.filterSongs(fetchSongs(""))
                Toast.makeText(context,"Filter Removed",Toast.LENGTH_SHORT).show()
                val dp:Float = 7.0f
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    resources.displayMetrics
                )
                val params=recView.layoutParams as ViewGroup.MarginLayoutParams
                //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayoutRv.getLayoutParams();
                params.setMargins(0,px.toInt(),0,0 )
                recView.layoutParams = params
            }
        }

        searchBar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(searchBar.visibility==View.VISIBLE)
                    //if(!queryActive)
                   querySongs(searchBar.text.toString())
                queryActive=true

            }

        })
    }

    private fun querySongs(queryKey: String) {
        songAdapter.filterSongs(fetchSongs(queryKey))
    }

    private fun fetchSongs(queryKey: String): ArrayList<Song> {
        val songsList=ArrayList<Song>()
        val songUri:Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection= arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID)

        val sortOrder:String = when(sortId){
            "Title" -> MediaStore.Audio.Media.TITLE
            "Artist" -> MediaStore.Audio.Media.ARTIST
            "Duration" -> MediaStore.Audio.Media.DURATION
            "Size" -> MediaStore.Audio.Media.SIZE
            "Date Added" -> MediaStore.Audio.Media.DATE_ADDED
            else -> MediaStore.Audio.Media.TITLE
        }

        val cursor=requireContext().contentResolver.query(songUri,projection,null,null,sortOrder)
        val idColumn=cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val durationColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val sizeColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        val artistColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

        while (cursor.moveToNext())
        {
            val id:Long=cursor.getLong(idColumn)
            var name:String=cursor.getString(nameColumn)

            val duration:Int=cursor.getInt(durationColumn)
            val size:Int=cursor.getInt(sizeColumn)
            val artist:String=cursor.getString(artistColumn)
            val albumId:Long=cursor.getLong(albumColumn)
            if(!(name.contains(queryKey,ignoreCase = true)||artist.contains(queryKey,ignoreCase = true)))
                continue
            //Log.i("albumId",albumId.toString()+"//"+name)
            //fetchAlbumArt(albumId)
            val uri:Uri=ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)

            val art:Uri? = if(albumId!=0L && albumId>0L)
                ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumId)
            else
                null
            name=name.substring(0,name.lastIndexOf('.'))

            val song:Song=Song(name,uri,art!!,artist,size,duration)
            songsList.add(song)
        }
        cursor.close()
        return songsList
        //showSongs(songsList)
        }

    private fun showSongs(songsList: ArrayList<Song>) {
        if(songsList.size==0)
        {
            Toast.makeText(context,"No Songs Detected",Toast.LENGTH_SHORT).show()
            return
        }
        songRecList.clear()
        songRecList.addAll(songsList)

        recView.layoutManager=LinearLayoutManager(context)
        songAdapter= SongAdapter(requireContext(),musicPlayer)
        songAdapter.setHasStableIds(true)
        recView.adapter=songAdapter
        recView.isNestedScrollingEnabled=false

        val scaleInAnimation: ScaleInAnimationAdapter= ScaleInAnimationAdapter(songAdapter)
        scaleInAnimation.setDuration(500)
        scaleInAnimation.setFirstOnly(false)
        scaleInAnimation.setInterpolator(OvershootInterpolator())
        recView.adapter=scaleInAnimation
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        //TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}