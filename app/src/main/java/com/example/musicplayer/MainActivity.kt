package com.example.musicplayer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.airbnb.lottie.LottieAnimationView

class MainActivity : AppCompatActivity() {

    private lateinit var text2:TextView
    private lateinit var back:androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var animView: LottieAnimationView
    private val splash_Timeout:Int=3000
    var i=0
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        text2=findViewById(R.id.text2)
        text2.visibility=View.GONE
        animView=findViewById(R.id.animView)
        back=findViewById(R.id.backView)
        val backAnim =AnimationUtils.loadAnimation(this,R.anim.slide_in_right)
        back.startAnimation(backAnim)
        Handler(Looper.getMainLooper()).postDelayed({
            val splashIntent = Intent(this@MainActivity,HomeActivity::class.java)
            startActivity(splashIntent)
            overridePendingTransition(R.anim.fadein,R.anim.fadeout)
            finish()
        }, splash_Timeout.toLong())
        /*val anim1 =AnimationUtils.loadAnimation(this,R.anim.anim2)
        text1.startAnimation(anim1)
        val anim2 =AnimationUtils.loadAnimation(this,R.anim.animation)
        text2.startAnimation(anim2)*/
        backAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationRepeat(p0: Animation?) {
//
            }

            override fun onAnimationEnd(p0: Animation?) {
                //val anim2 =AnimationUtils.loadAnimation(this@MainActivity,R.anim.animation)
                text2.visibility=View.VISIBLE
                animateText(text2.text as String)
                animView.playAnimation()
            }
        })
        }

    private fun animateText(text: String) {

        if(i<=text.length)
        {
            val fetchtext:String=text.substring(0,i);
            text2.text=fetchtext
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    i++;
                    animateText(text)
                },100
            )
        }

    }
}
