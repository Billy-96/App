package com.template

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView

class WebActivity : AppCompatActivity() {
    lateinit var webView:WebView
    var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        loadData()

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true)
        webView = findViewById(R.id.web_view)

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url!!)

    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }
    }

    fun loadData() {
        val sharedPreference = getSharedPreferences("savedPrefs", Context.MODE_PRIVATE)
        val savedString = sharedPreference.getString("url", null)
        url = savedString
        if (url == null) {
            url = intent.getStringExtra("url")
        }
        if (url == null) {
            startActivity(Intent(this, MyInitialActivity::class.java))
            setData(intent.getStringExtra("uuid").toString())
        }
    }
    fun setData(uuid:String){
        val sharedPreference = getSharedPreferences("savedPrefs", Context.MODE_PRIVATE)
        sharedPreference.edit().apply{
            putString("uuid",uuid)
            putString("url",url)
        }
    }
}