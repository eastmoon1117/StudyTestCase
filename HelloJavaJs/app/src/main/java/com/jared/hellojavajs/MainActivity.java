package com.jared.hellojavajs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);

        initWebView();

        //initJsBridge();

    }

    private void initJsBridge() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new JSBridgeWebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
        JSBridge.register("bridge", BridgeImpl.class);
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(), "control");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                testControl();
            }
        });
        webView.loadUrl("file:///android_asset/test.html");
    }

    private void testControl() {
        String control = "javascript:helloJava()";
        control = "javascript:helloJavaWithParam(\""+"param1"+"\")";
        control = "javascript:helloToJava()";
        webView.loadUrl(control);
    }

    public class JsInterface {

        @JavascriptInterface
        public void helloJs(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            //Log.d("MainActivity", "hellojs");
        }
    }
}
