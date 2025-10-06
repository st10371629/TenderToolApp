package com.tendertool.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.util.Log

class GoogleSignInSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.partial_google_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.googleWebView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://accounts.google.com/signin")

        Log.d("GoogleSignInSheet", "WebView loaded Google Sign-In page")
    }
}
