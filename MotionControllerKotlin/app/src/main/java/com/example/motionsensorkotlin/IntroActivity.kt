package com.example.motionsensorkotlin

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.motionsensorkotlin.Intro.IntroPart
import kotlinx.android.synthetic.main.activity_intro.*



class IntroActivity : AppCompatActivity() {

    private lateinit var uniqueID : String
    var introPart : IntroPart = IntroPart()
    private val intent_uniqueID = "intent_uniqueID"

    val DURATION : Long = 2000 // 1초 대기하고 이동하도록 설정


    private var AniSet : AnimationSet = AnimationSet(true)


//    private val constlayout : ConstraintLayout = IntroLayout
//    private val aniGradient : AnimationDrawable =constlayout.background as AnimationDrawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //full screen
        try {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (e: NullPointerException) {}


        setContentView(R.layout.activity_intro)
        var constlayout : ConstraintLayout = IntroLayout
        val aniGradient : AnimationDrawable =constlayout.background as AnimationDrawable




        aniGradient.setEnterFadeDuration(2000)
        aniGradient.setExitFadeDuration(4500)
        aniGradient.start()
        uniqueID = introPart.getAppInstanceId()



        // 생성한 ID를 Toast 로 한번 출력
        //Toast.makeText(this, "ID : " + uniqueID, Toast.LENGTH_LONG).show()

        // 아래 잠시 대기하도록 하여 1초 있다가 MainActivity 로 전환이 되도록 설정
//        Handler().postDelayed({val intent = Intent(this, ConnectControllerActivity::class.java)
//
//            intent.putExtra(intent_uniqueID, uniqueID)
//            startActivity(intent)
//            finish()
//        },DURATION)


        //깜빡이게
        var alpha : Animation = AnimationUtils.loadAnimation(this,R.anim.alpha)
        AniSet.addAnimation(alpha)
        PleaseTab.startAnimation(AniSet)


        IntroLayout.setOnClickListener {
            val intent = Intent(this, ConnectControllerActivity::class.java)
            intent.putExtra(intent_uniqueID, uniqueID)
            startActivity(intent)
            finish()
        }


    }



}