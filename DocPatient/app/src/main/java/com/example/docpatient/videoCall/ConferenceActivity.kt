package com.example.docpatient.videoCall


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.docpatient.R
import com.example.docpatient.databinding.ActivityConferenceBinding
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment


class ConferenceActivity : AppCompatActivity() {
    lateinit var meetingId:String
    lateinit var userName:String
    private lateinit var binding: ActivityConferenceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityConferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        meetingId=intent.getStringExtra("MEETING_ID").toString()
        userName=intent.getStringExtra("USERNAME").toString()

        binding.meetId.setText("Meeting-ID: $meetingId")

        addFragment()




    }
    fun addFragment() {
        val appID: Long = AppConstants.appId
        val appSign: String = AppConstants.appSign

        val conferenceID: String = meetingId
        val userID: String = userName
        val userName = userName

        val config = ZegoUIKitPrebuiltVideoConferenceConfig()
        val fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
            appID,
            appSign,
            userID,
            userName,
            conferenceID,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_Container, fragment)
            .commitNow()
    }
}