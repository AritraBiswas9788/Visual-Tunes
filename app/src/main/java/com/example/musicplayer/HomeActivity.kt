package com.example.musicplayer

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.wwdablu.soumya.lottiebottomnav.*


class HomeActivity : AppCompatActivity(), ILottieBottomNavCallback {
    private lateinit var songTitle: TextView
    private lateinit var menuIcon: LottieAnimationView
    //private lateinit var playButton: LottieAnimationView
    private lateinit var drawer: DrawerLayout
    private lateinit var appIcon: LottieAnimationView
    private lateinit var navMenu: com.google.android.material.navigation.NavigationView
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter: FragmentBodyAdapter
    private lateinit var lottieNav: LottieBottomNav
    private lateinit var playerBar: PlayerBar
    //private lateinit var likeButton: LottieAnimationView
    private lateinit var exoPlayer: androidx.media3.exoplayer.ExoPlayer
    private var storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted)
                userResponses()

        }
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        songTitle = findViewById(R.id.SongTitle)
        menuIcon = findViewById(R.id.menuIcon)
        //playButton = findViewById(R.id.PlayButton)
        navMenu = findViewById(R.id.NavMenu)
        drawer = findViewById(R.id.drawer)
        lottieNav = findViewById(R.id.LottieNavBar)
        //likeButton = findViewById(R.id.likeButton)
        viewPager = findViewById(R.id.FragmentPager)
        exoPlayer = androidx.media3.exoplayer.ExoPlayer.Builder(this).build()
        fragmentAdapter = FragmentBodyAdapter(supportFragmentManager, lifecycle)
        playerBar = PlayerBar(
            this,
            findViewById(R.id.SongName),
            findViewById(R.id.SongInfo),
            findViewById(R.id.PlayButton),
            findViewById(R.id.likeButton),
            findViewById(R.id.listenModeButton),
            exoPlayer
        )

        storagePermissionLauncher.launch(permission)
        viewPager.adapter = fragmentAdapter
        viewPager.isUserInputEnabled = false
        appIcon = navMenu.getHeaderView(0).findViewById(R.id.appIcon)
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        songTitle.isSelected = true
        var isClicked = false
        exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                pulseTitleAnimator(isPlaying)
            }
        })
        //Set font item
        var fontItem: FontItem = FontBuilder.create("Dashboard")
            .selectedTextColor(Color.WHITE)
            .unSelectedTextColor(Color.GRAY)
            .selectedTextSize(14) //SP
            .unSelectedTextSize(12) //SP
            .setTypeface(ResourcesCompat.getFont(this, R.font.happy_monkey))
            .build()
        //Menu Dashboard
        val item1: MenuItem =
            MenuItemBuilder.create("homeicon.json", MenuItem.Source.Assets, fontItem, "dash")
                .pausedProgress(1f)
                .loop(false)
                .build()
        //Example Spannable String (at Menu Gifts)
        /*val spannableString = SpannableString("Library")
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )*/
        //Menu Gifts
        fontItem = FontBuilder.create(fontItem).setTitle("Library").build()
        val item2: MenuItem = MenuItemBuilder.createFrom(item1, fontItem)
            .selectedLottieName("library.json")
            .unSelectedLottieName("library.json")
            .loop(false)
            .build()
        //Menu Mail
        fontItem = FontBuilder.create(fontItem).setTitle("Visualizer").build()
        val item3: MenuItem = MenuItemBuilder.createFrom(item1, fontItem)
            .selectedLottieName("visualizericon.json")
            .unSelectedLottieName("visualizericon.json")
            .loop(false)
            .build()
        //Menu Settings
        fontItem = FontBuilder.create(fontItem).setTitle("Songs").build()
        val item4: MenuItem = MenuItemBuilder.createFrom(item1, fontItem)
            .selectedLottieName("soundicon.json")
            .unSelectedLottieName("soundicon.json")
            .loop(false)
            .build()

        val list: ArrayList<MenuItem> = ArrayList(4)
        list.add(item1)
        list.add(item2)
        list.add(item3)
        list.add(item4)

        lottieNav.setCallback(this)
        lottieNav.setMenuItemList(list)
        lottieNav.selectedIndex = 0
        menuIcon.setOnClickListener {
            isClicked = if (!isClicked) {
                appIcon.playAnimation()
                menuIcon.setMinAndMaxProgress(0.0f, 0.5f)
                menuIcon.playAnimation()
                drawer.openDrawer(GravityCompat.START)
                true
            } else {
                menuIcon.setMinAndMaxProgress(0.5f, 1.0f)
                menuIcon.playAnimation()
                drawer.closeDrawer(GravityCompat.START)
                false
            }
        }
        /*var isPlayed = false
        playButton.setOnClickListener {
            if (!isPlayed) {
                //Toast.makeText(this,"Should pause",Toast.LENGTH_SHORT).show()
                playButton.speed = 1.0f
                playButton.playAnimation()
                isPlayed = true

            } else {
                //Toast.makeText(this,"Should play",Toast.LENGTH_SHORT).show()
                playButton.speed = -1.0f
                playButton.playAnimation()
                isPlayed = false

            }


        }
        var isLiked = false
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
        }*/
        drawer.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                if (!menuIcon.isAnimating) {
                    appIcon.playAnimation()
                    menuIcon.setMinAndMaxProgress(0.0f, 0.5f)
                    menuIcon.playAnimation()
                    drawer.openDrawer(GravityCompat.START)
                    isClicked = true
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                menuIcon.setMinAndMaxProgress(0.5f, 1.0f)
                menuIcon.playAnimation()
                isClicked = false

            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    fun pulseTitleAnimator(playing: Boolean) {
        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            songTitle,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown.duration = 310
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE
        if (playing) {
            scaleDown.start()
        } else {
            scaleDown.end()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (exoPlayer.isPlaying)
            exoPlayer.stop()
        exoPlayer.release()
    }

    fun sendMainAudioPlayer(): androidx.media3.exoplayer.ExoPlayer {
        return exoPlayer
    }

    fun sendPlayerBar():PlayerBar
    {
        return playerBar
    }

    private fun userResponses() {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else
            if (shouldShowRequestPermissionRationale(permission)) {
                AlertDialog.Builder(this)
                    .setTitle("REQUESTING PERMISSION")
                    .setMessage("Storage Permission is required to fetch songs from your device")
                    .setPositiveButton("ALLOW", DialogInterface.OnClickListener { dialog, which ->
                        storagePermissionLauncher.launch(permission)
                    })
                    .setNegativeButton("DENY", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this, "STORAGE PERMISSION WAS DENIED", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    })
                    .show()

            } else {
                Toast.makeText(this, "STORAGE PERMISSION WAS DENIED", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onMenuSelected(oldIndex: Int, newIndex: Int, menuItem: MenuItem?) {
        /*when(newIndex) {
            0 -> Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show()
            1 -> Toast.makeText(this,"Library",Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this,"Visualizer",Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(this,"Songs",Toast.LENGTH_SHORT).show()
        }*/
        viewPager.currentItem = newIndex


    }

    override fun onAnimationStart(index: Int, menuItem: MenuItem?) {
    }

    override fun onAnimationEnd(index: Int, menuItem: MenuItem?) {
    }

    override fun onAnimationCancel(index: Int, menuItem: MenuItem?) {
    }

}