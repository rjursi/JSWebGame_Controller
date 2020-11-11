package com.example.motionsensorkotlin

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.motionsensorkotlin.IOSocket.IoSocket


class DialogManager(context: Context, Socket: IoSocket)  {

    val context = context
    private var dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    val IoSocketConn =Socket

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var windowManager : WindowManager.LayoutParams




    init {
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setCancelable(false)    //다이얼로그 밖을 누를 때 안닫히도록

        var displayMetric = context.resources.displayMetrics
        var width = displayMetric.widthPixels
        var height = displayMetric.heightPixels

        var wm :WindowManager.LayoutParams = dlg.window!!.attributes
        wm.width = width/2
        wm.height = height/2
        dlg.window!!.attributes= wm

    }

    //채팅 보내기 위한 dialog
    fun makeSendingMessageDialog(){

        var view = inflater.inflate(R.layout.dialog_sendingmessage, null)

        dlg.setContentView(view)
        dlg.setTitle("채팅 입력")

        var btnSend = dlg.findViewById<Button>(R.id.message_btnSend)
        var btnCancel = dlg.findViewById<Button>(R.id.message_btnCancel)
        var txtMessage = dlg.findViewById<EditText>(R.id.message_edit).text
        btnSend.setOnClickListener {
            IoSocketConn.sendChatMessage(txtMessage.toString())
            Toast.makeText(context,txtMessage.toString(),Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    //초대 코드 입력을 위한 dialog
    fun makeInviteCodeDialog(){
        var view = inflater.inflate(R.layout.dialog_inputinvitecode, null)

        dlg.setContentView(view)
        dlg.setTitle("초대 코드 입력")

        var btnSend = dlg.findViewById<Button>(R.id.invide_btnSend)
        var btnCancel = dlg.findViewById<Button>(R.id.invite_btnCancel)
        var txtInviteCode = dlg.findViewById<EditText>(R.id.invite_edit).text

        btnSend.setOnClickListener {
            IoSocketConn.sendJoinToInviteCode(txtInviteCode.toString())
            Toast.makeText(context,txtInviteCode.toString(),Toast.LENGTH_LONG).show()
            dlg.dismiss()
        }
        btnCancel.setOnClickListener { dlg.dismiss() }

        dlg.show()
    }


}