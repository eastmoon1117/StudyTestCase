package com.jared.layoutoptimise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewStub;

public class MainActivity extends AppCompatActivity {

    private ViewStub viewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_test_relative_layout);
//        setContentView(R.layout.activity_main);
//        viewStub = (ViewStub) findViewById(R.id.vs_test);
//        findViewById(R.id.btn_view_show).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewStub.setVisibility(View.VISIBLE);
//            }
//        });
//        findViewById(R.id.btn_view_hide).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewStub.setVisibility(View.GONE);
//            }
//        });
    }
}
