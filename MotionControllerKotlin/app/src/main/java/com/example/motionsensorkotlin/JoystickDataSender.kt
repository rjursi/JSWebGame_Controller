package com.example.motionsensorkotlin

import android.provider.Settings
import android.util.Log
import com.example.motionsensorkotlin.IOSocket.IoSocket
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class JoystickDataSender (ioSocketConn : IoSocket) {

    var joystickDataSender_coroutine = GlobalScope
    var isRunning = false
    val ioSocketConn = ioSocketConn
    var nowDirectionData : Double = 0.0

    fun sendJoystickData_runCoroutine(){

        isRunning = true
        joystickDataSender_coroutine.launch {
            while(true){

                if(nowDirectionData == 0.0){
                    ioSocketConn.sendJoystickData(nowDirectionData)
                    isRunning = false
                    Log.d("Coroutine Status", "Coroutine is Canceled.")
                    break
                }

                if(nowDirectionData == 0.5){
                    ioSocketConn.sendJoystickData(nowDirectionData)
                    isRunning = false
                    Log.d("Coroutine Status", "Coroutine is Canceled.")
                    break
                }

                Log.d("Coroutine Status", "Coroutine is Running...")
                ioSocketConn.sendJoystickData(nowDirectionData)
                delay(10L)

            }
        }

    }

    fun coroutine_runningCheck(){
        if(!isRunning){
            sendJoystickData_runCoroutine()
        }
    }


}