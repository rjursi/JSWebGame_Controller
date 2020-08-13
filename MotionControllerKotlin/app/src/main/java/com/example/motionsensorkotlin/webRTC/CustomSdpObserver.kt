package com.example.motionsensorkotlin.webRTC

import android.util.Log

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class CustomSdpObserver(logtag:String):SdpObserver{

    val tag:String = this.javaClass.canonicalName!! +" "+logtag

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Log.d(
            tag,
            "onCreateSuccess() called with: sessionDescription = [$sessionDescription]"
        )
    }

    override fun onSetSuccess() {
        Log.d(tag, "onSetSuccess() called")
    }

    override fun onCreateFailure(s: String) {
        Log.d(tag, "onCreateFailure() called with: s = [$s]")
    }

    override fun onSetFailure(s: String) {
        Log.d(tag, "onSetFailure() called with: s = [$s]")
    }


}