package com.kk.gourmetapp.setting

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Switch
import com.airbnb.lottie.LottieAnimationView
import com.kk.gourmetapp.R
import com.kk.gourmetapp.util.PreferenceUtil

class SettingActivity : AppCompatActivity() {

    var mIsChage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // セレブモード用トグルスイッチ
        val celebSwitch: Switch = findViewById(R.id.celeb_switch)
        val preference: SharedPreferences = getSharedPreferences(
            PreferenceUtil.KEY_PREFERENCE_SETTING, Context.MODE_PRIVATE)
        celebSwitch.isChecked = preference.getBoolean(
            PreferenceUtil.KEY_PREFERENCE_CELEB_MODE, false)

        // セレブモードがONだったらアニメーション開始
        val celebBackground: LottieAnimationView = findViewById(R.id.celeb_background)
        if (celebSwitch.isChecked) {
            celebBackground.visibility = View.VISIBLE
        }

        celebSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                // セレブモードonで保存
                preference.edit().putBoolean(PreferenceUtil.KEY_PREFERENCE_CELEB_MODE, true).apply()
                celebBackground.visibility = View.VISIBLE
                mIsChage = true
            } else {
                // セレブモードoffで保存
                preference.edit().putBoolean(
                    PreferenceUtil.KEY_PREFERENCE_CELEB_MODE, false).apply()
                celebBackground.visibility = View.GONE
                mIsChage = true
            }
        }
    }

    override fun finish() {
        if (mIsChage) {
            setResult(Activity.RESULT_OK)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        super.finish()
    }
}
