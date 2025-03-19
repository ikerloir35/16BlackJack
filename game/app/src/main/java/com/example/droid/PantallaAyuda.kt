package com.example.droid

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PantallaAyuda: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayuda)


        val myWebView: WebView = findViewById(R.id.webView)
        myWebView.webViewClient = WebViewClient()


        myWebView.settings.javaScriptEnabled = true

        myWebView.loadUrl("file:///android_asset/Help.html")
    }
}