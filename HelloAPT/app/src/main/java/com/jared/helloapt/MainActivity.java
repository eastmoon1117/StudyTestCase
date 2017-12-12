package com.jared.helloapt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jared.annotation.BindView;
import com.jared.annotation.OnClick;
import com.jared.viewfinder.ViewFinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_info)
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewFinder.inject(this);
    }

    @OnClick({R.id.bt_change, R.id.bt_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change:
                tvInfo.setText("Hello APT!");
                break;
            case R.id.bt_reset:
                tvInfo.setText("Hello World!");
                break;
        }
    }
}
