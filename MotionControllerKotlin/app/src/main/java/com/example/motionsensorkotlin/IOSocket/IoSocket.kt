package com.example.motionsensorkotlin.IOSocket

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import kotlinx.coroutines.delay
import org.json.JSONObject
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.motionsensorkotlin.MainActivity
import org.json.JSONException
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class  IoSocket (controllerActivity : Activity){




// class IoSocket (mainActivity : Activity) {

    val opts = IO.Options()


    lateinit var mSocket: Socket
    lateinit var username: String
    lateinit var gamesockId: String
    var users: Array<String> = arrayOf()

    var inviteCode : String = ""    //roomName
    // private var mainActivity : Activity = mainActivity


    private var roomName:String?=null

    //////////////////////////////////

    //////////////////////////////////

    fun connectIoServer(gamesockId : String){
        this.gamesockId = gamesockId
        opts.reconnection = false
        mSocket = IO.socket("https://jswebgame.run.goorm.io", opts)

//        try{
//            mSocket = IO.socket("https://jswebgame.run.goorm.io")
//
//        } catch(e: URISyntaxException){
//            Log.e("IOSocket", e.reason)
//        }

        mSocket.connect()
        // server 측의 io.on('connection', function (socket) {-} 을 따라감
        // mSocket.emit('connection',socket)을 한 것 과 동일하다고 할 수 있음

        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        // 위 연결이 성공적으로 연결이 되면 server 측에서 "connect" 이벤트를 발생

        mSocket.on("ad_invalidInviteCode", onInvalidInviteCode)
    }

    var onInvalidInviteCode : Emitter.Listener = Emitter.Listener { args ->
        val serverErrorMsg = args[0] as String

        Log.d("serverErrorMsg : ", serverErrorMsg)
    }



    val onConnect: Emitter.Listener = Emitter.Listener {
        // login 이벤트를 서버쪽으로 같이 보낼 예정


        // 로그인 한다는 이벤트를 보냄
        mSocket.emit("controller_connect", gamesockId)
        Log.d("IOSocket", "Socket is Connected with $gamesockId")


        mSocket.on("server_inviteCode", onGetInviteCode)
        
        // 서버 측에서 웹 페이지가 종료되었다는 신호를 보낼 경우
        mSocket.on("server_Disconnected", onDisconnected)

    }

    private val onDisconnected : Emitter.Listener = Emitter.Listener {
        controllerActivity.runOnUiThread(Runnable {
            val builder = AlertDialog.Builder(controllerActivity)

            // Toast.makeText(mainActivity, "Server Disconnected", Toast.LENGTH_LONG).show()
            builder.setTitle("웹 게임 페이지 종료")
            builder.setMessage("웹 페이지 상의 게임이 종료 되어서 컨트롤러를 종료합니다.")

            builder.setPositiveButton("Yes"){dialog, which ->

                controllerActivity.moveTaskToBack(true)
                controllerActivity.finishAndRemoveTask()

                android.os.Process.killProcess(android.os.Process.myPid())

                /*
                finishAffinity(mainActivity)
                System.runFinalization()
                System.exit(0)

                 */
            }

            val dialog: AlertDialog = builder.create()


            dialog.show()
        })

    }

    // 서버에서 만들고 보낸 초대 코드를 안드로이드 안에서도 사용이 가능하도록 처리
    private val onGetInviteCode : Emitter.Listener = Emitter.Listener{ args ->

        inviteCode = args[0] as String

        Log.d("Received InviteCode", inviteCode)
        Log.d("Message : " , "Get InviteCode Success")
        roomName=inviteCode
    }

    fun sendJoinToInviteCode(inviteCode : String){
        Log.d("Send inviteCode", inviteCode)

        var joinDataJson = JSONObject()
        joinDataJson.put("inviteCode", inviteCode);
        joinDataJson.put("gamesocketId", gamesockId);

        mSocket.emit("ad_joinTothePlayer1Room", joinDataJson)
    }

    fun sendJoystickData(data : Double){
        mSocket.emit("ad_joystickData", data)
    }

    // 가속도 센서 데이터 보내는 함수
    fun sendAccData(data : JSONObject){
        mSocket.emit("ad_AccData", data)
    }

    // 자이로스코프 센서 데이터 보내는 함수

    fun sendGyroData(data : JSONObject){
        mSocket.emit("ad_GyroData",data)
    }

    fun sendTypingSignal(){
        mSocket.emit("ad_TypingSignal")
    }

    fun sendChatMessage(message: String){
        mSocket.emit("ad_ChatMessage", message)
    }

    fun sendLogoutMsg(){
        mSocket.emit("ad_logout", gamesockId)
    }

    fun sendPauseMsg(){
        mSocket.emit("ad_pause", gamesockId)
    }

    fun sendStopMsg(){
        mSocket.emit("ad_stop", gamesockId)
    }

    fun sendRestartMsg(){
        mSocket.emit("ad_restart", gamesockId)
    }


    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////







}