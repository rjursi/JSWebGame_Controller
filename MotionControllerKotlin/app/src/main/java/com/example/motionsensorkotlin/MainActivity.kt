package com.example.motionsensorkotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
//import androidx.test.espresso.core.internal.deps.guava.collect.Range.closed
import com.example.motionsensorkotlin.IOSocket.IoSocket
import com.example.motionsensorkotlin.SensorListener.AccelerometerSensorListener
import com.example.motionsensorkotlin.SensorListener.GyroScopeSensorListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.connectCtrlBtn

import kotlinx.android.synthetic.main.dialog_inputinvitecode.view.*
import java.io.IOException


class MainActivity : AppCompatActivity(){




    lateinit var introIntent : Intent
    lateinit var app_unique_id : String


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        // ? : Null 일 수 있음을 지칭함
        // savedInstanceState : 액티비티의 이전 상태, 즉 잠시 어플리케이션을 나갓다 오거나 어플리케이션의 이전 상태

        // 화면이 꺼지지 않도록 설정
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 화면이 세로 모드로 고정이 되도록 지정
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        introIntent = intent // getIntent 역할
        app_unique_id = intent.getStringExtra("intent_uniqueID")


        connectCtrlBtn.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        var UrlRightCheck : Boolean

        if(result != null){
            if(result.contents == null){
                Toast.makeText(this, "Cancelled" + result.contents, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Scanned : " + result.contents, Toast.LENGTH_SHORT).show()

                // qr 코드로 부터 url 과 해당 game web socket을 받음
                val idPutUrl = result.contents
                UrlRightCheck = idPutUrl.contains("https://jswebgame.run.goorm.io", false)

                if(!UrlRightCheck){
                    Toast.makeText(this, "Wrong URL value", Toast.LENGTH_SHORT).show()
                }
                else{
                    //var mainIntent = Intent(this, MainActivity::class.java)
                    val mainIntent = Intent(applicationContext, ControllerActivity::class.java)

                    // 여기 아래에서 소켓 ID를 구분
                    val gamesocketId = idPutUrl.substring(idPutUrl.lastIndexOf("=")+1)

                    mainIntent.putExtra("gamesocketId", gamesocketId)
                    startActivity(mainIntent)
                    finish()
                }

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
