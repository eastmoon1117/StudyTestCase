package com.jared.jnidaemon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jared.daemon.Daemon;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    NdkJniUtils jniUtils = new NdkJniUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jniUtils.nativeInit();
        jniUtils.nativeClassInit();
        jniUtils.startThread();

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(jniUtils.stringFromJNI() + ":" + jniUtils.sumFromJNI(4, 3));

        findViewById(R.id.start_daemon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Daemon.createSocketFile(getApplication());
                Daemon.run(getApplication(), "");
                //tv.setText(jniUtils.stringFromJNI() + ":" + jniUtils.sumFromJNI(4, 3));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jniUtils.nativeCleanup();
    }
}
