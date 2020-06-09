package com.example.motionsensorkotlin

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import androidx.appcompat.app.AppCompatActivity
import com.example.motionsensorkotlin.Intro.IntroPart
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class IntroActivity : AppCompatActivity() {

    private lateinit var uniqueID : String
    var introPart : IntroPart = IntroPart()
    private val intent_uniqueID = "intent_uniqueID"

    val DURATION : Long = 2000 // 1초 대기하고 이동하도록 설정

    private var fadeIn :AlphaAnimation = AlphaAnimation(0.0f, 1.0f)
    private var fadeout :AlphaAnimation = AlphaAnimation(1.0f, 0.0f)

    private var fadeAni :AlphaAnimation = AlphaAnimation(1.0f, 0.0f)
    private var AnimatioSet : AnimationSet = AnimationSet(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //hides title bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}



        setContentView(R.layout.activity_intro)

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



        fadeIn.duration=2000
        fadeout.duration=2000

        fadeAni.duration=2000
        AnimatioSet.addAnimation(fadeAni)



//        AnimatioSet.addAnimation(fadeout)
//        AnimatioSet.startOffset=0
//        AnimatioSet.addAnimation(fadeIn)

        var job = GlobalScope.launch(Dispatchers.Default){
            repeat(10){
                AnimatioSet.start()
            }
        }

        job.start()

        IntroLayout.setOnClickListener {
            val intent = Intent(this, ConnectControllerActivity::class.java)
            intent.putExtra(intent_uniqueID, uniqueID)
            startActivity(intent)
            finish()
        }


    }



}