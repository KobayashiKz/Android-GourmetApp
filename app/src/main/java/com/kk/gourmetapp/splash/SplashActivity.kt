package com.kk.gourmetapp.splash

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.kk.gourmetapp.R

class SplashActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_DURATION: Long = 2200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        handler.postDelayed( {
            // 指定時間後にフェードアウトで閉じる
            setResult(Activity.RESULT_OK)
            finish()
            overridePendingTransition(0, R.anim.alpha_fadeout)
        }, SPLASH_DURATION)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
