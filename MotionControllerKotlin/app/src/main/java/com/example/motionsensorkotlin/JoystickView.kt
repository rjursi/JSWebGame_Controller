package com.example.motionsensorkotlin

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import com.example.motionsensorkotlin.IOSocket.IoSocket
import com.example.motionsensorkotlin.SensorListener.GyroScopeSensorListener
import kotlinx.android.synthetic.main.activity_controller.view.*

class JoystickView : SurfaceView, SurfaceHolder.Callback, OnTouchListener {
    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var hatRadius = 0f


    var isfirstTouch = true//조이스틱의 유동적 위치 조정을 위한 변수

    private fun setupDimensions() {
        centerX = width / 2.toFloat()
        centerY = width / 2.toFloat()
        baseRadius =
            Math.min(width, height) / 4.toFloat() // Math.min함수는 둘중 작은것을 반환
        hatRadius = Math.min(width, height) / 6.toFloat()
    }
    var IoSocketConn : IoSocket
    lateinit var sensorManager : SensorManager

    constructor(context: Context?, IoSocket : IoSocket,sensorManagerParam: SensorManager) : super(context) {
        holder.addCallback(this)
        setOnTouchListener(this)
//        if (context is JoystickListener) {
//            joystickCallback = context
//        }
        //joystickCallback = context
        IoSocketConn = IoSocket
        sensorManager = sensorManagerParam
        gyroScopeSensorListener=
                GyroScopeSensorListener(
                    IoSocketConn
                )
    }

    var gyroScopeSensorListener : GyroScopeSensorListener

//    private val sensorManager by lazy{
//        // 지연된 초기화 사용
//        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        // sensorManager 변수를 처음 사용할 때 getSystemService() 메서드로 SensorManager 객체를 얻음
//    }

    private fun drawJoystick(newX: Float, newY: Float, alpha : Int) {
        if (holder.surface.isValid) {
            val myCanvas = this.holder.lockCanvas()
            val colors = Paint()
            //배경, 이거 안하면 까만 배경위에 컨트롤러 액티비티의 컴포넌트들이 둥둥떠다닐거임
            myCanvas.drawARGB(255,92,209,229)//backcolor

            //조이스틱 아래에 깔리는 친구, isFirstToush로 처음 터치할때 받은 위치값으로 위치지정
            //눌렸을때만 알파값을 1로하고 안눌러있을 때는 0.5
            colors.setARGB(alpha, 50, 50, 50)
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors)

            //조이스틱의 실질적인 조종하는 부분, centerX,Y기준
            //위와 마찬가지로 알파값 조정
            colors.setARGB(alpha, 255, 0, 0)
            myCanvas.drawCircle(newX, newY, hatRadius, colors)

            holder.unlockCanvasAndPost(myCanvas)
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        setupDimensions()
        drawJoystick(centerX, centerY,123)
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        Log.d("onTouchEvent : ", "JoystickTouched")
        if (v == this) {
            if (e.action != MotionEvent.ACTION_UP) {
                if(isfirstTouch){
                    centerX = e.x
                    centerY=e.y
                    isfirstTouch=false
                }
                val displacement = Math.sqrt(
                    Math.pow(
                        e.x - centerX.toDouble(),
                        2.0
                    ) + Math.pow(e.y - centerY.toDouble(), 2.0)
                ).toFloat()
                if (displacement < baseRadius) {
                    drawJoystick(e.x, e.y,255)
//                    joystickCallback!!.onJoystickMoved(
                    onJoystickMoved(

                        (e.x - centerX) / baseRadius,
                        (e.y - centerY) / baseRadius,
                        id
                    )
                    //joystickCallback.onJoystickMoved(e.getX()-centerX,centerY-e.getY(),centerX,centerY, baseRadius, getId());
                } else {
                    val ratio = baseRadius / displacement
                    val constrainedX = centerX + (e.x - centerX) * ratio
                    val constrainedY = centerY + (e.y - centerY) * ratio
                    drawJoystick(constrainedX, constrainedY,255)
//                    joystickCallback!!.onJoystickMoved(
                    onJoystickMoved(
                        (constrainedX - centerX) / baseRadius,
                        (constrainedY - centerY) / baseRadius,
                        id
                    )
                }
                sensorManager.registerListener(gyroScopeSensorListener, sensorManager.getDefaultSensor(
                    Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME)
            } else {
                centerX = width / 2.toFloat()
                centerY = width / 2.toFloat()
                drawJoystick(centerX, centerY,123)
//                joystickCallback!!.onJoystickMoved(0f, 0f, id)
                onJoystickMoved(0f, 0f, id)
                isfirstTouch=true

                sensorManager.unregisterListener(gyroScopeSensorListener)
            }
        }
        return true
    }

    fun onJoystickMoved(xPercent: Float, yPercent: Float, source: Int){
        var directionData : Double = 0.0
        if( (yPercent >= 0.3 || yPercent <= -0.3) || (xPercent >= 0.3 || xPercent <= -0.3)) {

            Log.d("Left Joystick", "X percent: $xPercent Y percent: $yPercent")

            if ((yPercent < 0.0 && yPercent > -1.0) && (xPercent > 0.0 && xPercent < 1.0)) {
                //tvLog.text = "1.5시 방향"
                directionData = 1.5
            }
            if ((yPercent <= 0.3 && yPercent >= -0.3) && (xPercent > 0.0 && xPercent <= 1.0)) {
                //tvLog.text = "3시방향"
                directionData = 3.0
            }
            if ((yPercent < 1.0 && yPercent > 0.0) && (xPercent > 0.0 && xPercent < 1.0)) {
                //tvLog.text = "4.5시 방향"
                directionData = 4.5
            }
            if ((yPercent > 0.0 && yPercent <= 1.0) && (xPercent >= -0.3 && xPercent <= 0.3)) {
                //tvLog.text = "6시방향"
                directionData = 6.0
            }
            if ((yPercent < 1.0 && yPercent > 0.0) && (xPercent > -1.0 && xPercent < 0.0)) {
                //tvLog.text = "7.5시 방향"
                directionData = 7.5
            }
            if ((yPercent <= 0.3 && yPercent >= -0.3) && (xPercent < 0.0 && xPercent >= -1.0)) {
               // tvLog.text = "9시방향"
                directionData = 9.0
            }
            if ((yPercent < 0.0 && yPercent > -1.0) && (xPercent > -1.0 && xPercent < 0.0)) {
                //tvLog.text = "10.5시 방향"
                directionData = 10.5
            }
            if ((yPercent >= -1.0 && yPercent < 0.0) && (xPercent >= -0.3 && xPercent <= 0.3)) {
                //tvLog.text = "12시방향"
                Log.d("direction : ","12시방향")
                directionData = 12.0
            }

            Log.d("DirectionData","Direction : $directionData")

            directionData = String.format("%.2f", directionData).toDouble()
            IoSocketConn.sendJoystickData(directionData)

        }

        if (yPercent == 0F && xPercent == 0F)
        {
           // tvLog.text = ""
        }
    }

//    interface JoystickListener {
//        fun onJoystickMoved(
//            xPercent: Float,
//            yPercent: Float,
//            source: Int
//        ) // void onJoystickMoved(float posX, float posY, float centroX, float CentroY, float radio, int source);
//    }



}