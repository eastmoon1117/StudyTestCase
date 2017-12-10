package com.jared.performancetool;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jared.performancetool.databinding.MainBinding;

public class MainActivity extends AppCompatActivity {

    MainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
    }

    private void initView() {
        binding.btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.launch(MainActivity.this);
            }
        });
    }
}
