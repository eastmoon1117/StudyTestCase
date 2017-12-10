package com.jared.performancetool;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jared.performancetool.databinding.WebViewBinding;

/**
 * Created by jared on 2017/1/10.
 */

public class WebViewActivity extends AppCompatActivity {

    WebViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview_layout);
        //Debug.startMethodTracing("perform");
        initView();
        //Debug.stopMethodTracing();
    }

    private void initView() {
        binding.webView.loadUrl("http://www.hoolay.cn/ihoolay");
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        context.startActivity(intent);
    }
}
