package com.example.icsdownload

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.icsdownload.ICSDownloader.MIME_TYPE_ICS
import com.example.icsdownload.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private var binding: FragmentWebViewBinding? = null
    val webViewFragmentViewModel: WebViewFragmentViewModel by viewModels()
    val webViewCustomClient = object : WebViewClient() {

    }
    val webViewCustomChromeClient = object : WebChromeClient() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        val view = binding?.root
        binding?.fragmentWebView?.apply {
            webViewClient = webViewCustomClient
            webChromeClient = webViewCustomChromeClient
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            setDownloadListener { url, _, _, mimeType, _ ->
                if (MIME_TYPE_ICS.equals(mimeType, ignoreCase = true)) {
                    Log.d(WEB_VIEW_FRAGMENT_TAG, "onDownloadStart: mimeType=$mimeType, url=$url")
                    context?.let { ICSDownloader.saveICSFile(url, it) }
                } else {
                    Log.e(
                        WEB_VIEW_FRAGMENT_TAG,
                        "onDownloadStart: mimeType (Not Supported)=$mimeType, url=$url"
                    )
                }
            }
        }

        binding?.loadButton?.setOnClickListener {
            binding?.fragmentWebView?.loadUrl(binding?.urlText?.text.toString() ?: "")
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        binding?.fragmentWebView?.loadUrl("https://add-to-calendar-button.com/#demo")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val WEB_VIEW_FRAGMENT_TAG = "web_view_fragment_tag"
    }
}