package com.jared.apktest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_test.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        setContentView(R.layout.activity_test)
        tvName.text = showStr()
        btNext.setOnClickListener {
            gotoSecondActivity()
        }
    }

    private fun showStr(): String {
        return "Hello，I am ApkTest!"
    }

    private fun gotoSecondActivity() {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
