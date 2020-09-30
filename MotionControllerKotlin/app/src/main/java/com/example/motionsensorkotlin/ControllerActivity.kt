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
import android.provider.Settings
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
import kotlinx.android.synthetic.main.activity_controller.*
import kotlinx.android.synthetic.main.activity_controller.view.*

import kotlinx.android.synthetic.main.dialog_inputinvitecode.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.IOException



class ControllerActivity : AppCompatActivity(), JoystickView.JoystickListener {
    // : - AppCompatActivity 클래스를 상속을 한다는 의미 (클래스 앞에 붙을 경우)

    private val sensorManager by lazy{
        // 지연된 초기화 사용
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // sensorManager 변수를 처음 사용할 때 getSystemService() 메서드로 SensorManager 객체를 얻음
    }

    // 앞 인트로 Activity 에서 보낸 User ID 값을 받기 위한 인텐트 설정
    lateinit var byConnIntent: Intent
    lateinit var gamesocketId : String
    var isSending : Boolean = false




    val IoSocketConn : IoSocket = IoSocket(this)

    val joystickDataSender : JoystickDataSender = JoystickDataSender(IoSocketConn)
    var accelerometerSensorListener : AccelerometerSensorListener =
        AccelerometerSensorListener(
            IoSocketConn
        )
    // 객체 생성 및 클래스 생성자를 통하여 초기화
    var gyroScopeSensorListener : GyroScopeSensorListener =
        GyroScopeSensorListener(
            IoSocketConn
        )
    lateinit var dialogManager : DialogManager

    var preDirectionData : Double = 0.0


    override fun onJoystickMoved(xPercent: Float, yPercent: Float, source: Int) {
        var directionData : Double = 0.0

        when (source) {
            R.id.joystickLeft ->
            {
                if( (yPercent >= 0.3 || yPercent <= -0.3) || (xPercent >= 0.3 || xPercent <= -0.3)) {

                    Log.d("Left Joystick", "X percent: $xPercent Y percent: $yPercent")

                    if ((yPercent < 0.0 && yPercent > -1.0) && (xPercent > 0.0 && xPercent < 1.0)) {
                        tvLog.text = "1.5시 방향"
                        directionData = 1.5
                    }

                    if ((yPercent <= 0.5 && yPercent >= -0.5) && (xPercent > 0.0 && xPercent <= 1.0)) {
                        tvLog.text = "3시방향"
                        directionData = 3.0
                    }
                    if ((yPercent < 1.0 && yPercent > 0.0) && (xPercent > 0.0 && xPercent < 1.0)) {
                        tvLog.text = "4.5시 방향"
                        directionData = 4.5
                    }
                    if ((yPercent > 0.0 && yPercent <= 1.0) && (xPercent >= -0.5 && xPercent <= 0.5)) {
                        tvLog.text = "6시방향"
                        directionData = 6.0
                    }
                    if ((yPercent < 1.0 && yPercent > 0.0) && (xPercent > -1.0 && xPercent < 0.0)) {
                        tvLog.text = "7.5시 방향"
                        directionData = 7.5
                    }
                    if ((yPercent <= 0.5 && yPercent >= -0.5) && (xPercent < 0.0 && xPercent >= -1.0)) {
                        tvLog.text = "9시방향"
                        directionData = 9.0
                    }
                    if ((yPercent < 0.0 && yPercent > -1.0) && (xPercent > -1.0 && xPercent < 0.0)) {
                        tvLog.text = "10.5시 방향"
                        directionData = 10.5
                    }
                    if ((yPercent >= -1.0 && yPercent < 0.0) && (xPercent >= -0.5 && xPercent <= 0.5)) {
                        tvLog.text = "12시방향"
                        directionData = 12.0
                    }







                    Log.d("DirectionData", "Direction : $directionData")

                    directionData = String.format("%.2f", directionData).toDouble()
                }else{
                    // 여기는 그냥 터치만 했을 경우 , 즉 상호작용용
                    tvLog.text = "상호작용";
                    directionData = 0.5
                    Log.d("DirectionData", "Direction : $directionData")

                    directionData = String.format("%.2f", directionData).toDouble()

                }


                if (yPercent == 0F && xPercent == 0F)
                {
                    tvLog.text = ""
                    directionData = 0.0
                }

                joystickDataSender.nowDirectionData = directionData
                joystickDataSender.coroutine_runningCheck()

            }


            }
        }


    var texto: TextView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        // ? : Null 일 수 있음을 지칭함
        // savedInstanceState : 액티비티의 이전 상태, 즉 잠시 어플리케이션을 나갓다 오거나 어플리케이션의 이전 상태

        // 화면이 꺼지지 않도록 설정
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 화면이 세로 모드로 고정이 되도록 지정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)


        // 앞 Intro Activity 에서 보낸 socket ID 값을 받음
        byConnIntent = intent
        gamesocketId = byConnIntent.getStringExtra("gamesocketId")

        accTestBtn.setOnTouchListener {_:View, event:MotionEvent ->
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    sensorManager.registerListener(gyroScopeSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME)
                }

                MotionEvent.ACTION_UP -> {
                    sensorManager.unregisterListener(gyroScopeSensorListener)
                }
            }

            true
        }

        controller_btn_invite.setOnClickListener {
            dialogManager.makeInviteCodeDialog()
        }

        controller_btn_chat.setOnClickListener {
            dialogManager.makeSendingMessageDialog()
        }

        dialogManager = DialogManager(this, IoSocketConn)


        // 서버 연결
        IoSocketConn.connectIoServer(gamesocketId)

        // 초대코드 서버로부터 받을때까지 대기하고 받으면 실행하는 코드

        runBlocking{
            launch {
                while (true) {
                    if (IoSocketConn.inviteCode === "") {
                        continue
                    } else {
                        viewMyInviteCode.setText(IoSocketConn.inviteCode);
                        break
                    }
                }
            }
        }


            // 해당 버튼을 누를때만 보내도록




    }


    // 어플리케이션을 잠시 내렸을 경우
    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(gyroScopeSensorListener)
        IoSocketConn.sendPauseMsg()
    }

    override fun onStop() {
        super.onStop()

        sensorManager.unregisterListener(gyroScopeSensorListener)
        IoSocketConn.sendStopMsg()
    }

    override fun onRestart() {
        super.onRestart()

        sensorManager.registerListener(gyroScopeSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL)
        IoSocketConn.sendRestartMsg()
    }

    override fun onDestroy() {
        IoSocketConn.sendLogoutMsg();
        super.onDestroy()
    }


    private fun showInputInviteCodePopUp(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_inputinvitecode, null);

        var dialog_listener = object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.d("EditText String", view.controller_btn_invite.text.toString())
                IoSocketConn.sendJoinToInviteCode(view.controller_btn_invite.text.toString())

            }
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("초대 코드 입력")
            .setPositiveButton("확인", dialog_listener)
            .setNegativeButton("취소",null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()


    }

}
