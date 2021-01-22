package com.jared.graytest

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewGray(window.decorView);

        setContentView(R.layout.activity_main)

        tv_dialog.setOnClickListener {
            MyDialog.Builder(this).create().show()
        }
    }

    private fun setViewGray(view: View) {
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }
}