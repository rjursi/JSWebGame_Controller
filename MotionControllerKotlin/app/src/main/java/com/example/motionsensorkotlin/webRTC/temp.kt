package com.example.motionsensorkotlin.webRTC

import android.content.Context
import org.webrtc.*
import java.util.ArrayList

class temp(context:Context)  {//context:Context

    private val rootEglBase by lazy { EglBase.create() }

    var iceServers : MutableList<PeerConnection.IceServer> = ArrayList()

    val peerConnectionFactory: PeerConnectionFactory by lazy {
        //Initialize PeerConnectionFactory globals.
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options : PeerConnectionFactory.Options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
            rootEglBase.eglBaseContext, /* enableIntelVp8Encoder */true, /* enableH264HighProfile */true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)

        PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()

    }


    var sdpConstraints: MediaConstraints? = null
    var localVideoTrack: VideoTrack? = null
    var localAudioTrack: AudioTrack? = null


    var localVideoView: SurfaceViewRenderer? = null
    var remoteVideoView: SurfaceViewRenderer? = null

    //private var hangup: Button? = null
    var localPeer: PeerConnection? = null

    var gotUserMedia: Boolean = false
    var isStart : Boolean = false
//    private val iceServer = listOf(
//        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
//            .createIceServer()
//    )





    fun setIceServer(){

        val iceStun = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer()
        val iceTurnUDP = PeerConnection.IceServer.builder("turn:192.158.29.39:3478?transport=udp")
            .setUsername("28224511:1379330808") //username
            .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA") //credential
            .createIceServer()
        val iceTurnTCP = PeerConnection.IceServer.builder("turn:192.158.29.39:3478?transport=tcp")
            .setUsername("1379330808")
            .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA")
            .createIceServer()
        iceServers.add(iceStun)
        iceServers.add(iceTurnUDP)
        iceServers.add(iceTurnTCP)
    }

    fun start(){

        //Now create a VideoCapturer instance.
        val videoCapturerAndroid: VideoCapturer? = createCameraCapturer(Camera1Enumerator(false))

        val audioConstraints = MediaConstraints()
        //Create MediaConstraints - Will be useful for specifying video and audio constraints.

        val videoSource: VideoSource

        //Create a VideoSource instance
//        if (videoCapturerAndroid != null) {
//            videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid)
//            localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
//        }

        //create an AudioSource instance
        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

//        videoCapturerAndroid?.startCapture(1024, 720, 30)
//        localVideoView?.visibility = View.VISIBLE
//        //create a videoRenderer based on SurfaceViewRenderer instance
//        val localRenderer = VideoRenderer(localVideoView)
//        // And finally, with our VideoRenderer ready, we
//        // can add our renderer to the VideoTrack.
//        localVideoTrack?.addRenderer(localRenderer)
//
//        localVideoView?.setMirror(true)
//        remoteVideoView?.setMirror(true)

        gotUserMedia = true
        if (SignallingClient.isInitiator) {
            isStart = true
        }

    }









    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        //find the front facing camera and return it.
        deviceNames
            .filter { enumerator.isFrontFacing(it) }
            .mapNotNull { enumerator.createCapturer(it, null) }
            .forEach { return it }

        return null
    }


}