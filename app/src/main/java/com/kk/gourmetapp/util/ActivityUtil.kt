package com.kk.gourmetapp.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

class ActivityUtil {

    companion object {
        // Activity起動のリクエストコード
        const val REQUEST_CODE_SPLASH: Int = 0
        const val REQUEST_CODE_RECOGNIZE: Int = 1
        const val REQUEST_CODE_SETTING: Int = 2

        // Intentで渡す値のキー
        const val KEY_GURUNAVI_ADDRESS: String = "key_gurunavi_address"
        const val KEY_HOTPEPPER_ADDRESS:String = "key_hotpepper_address"

        /**
         * ActivityにFragmentを追加する際に必要なTransaction処理
         * @param fragmentManager
         * @param fragment 追加するフラグメント
         * @param frameId 適用するレイアウトID
         */
        fun addFragmentToActivity(fragmentManager: FragmentManager, fragment: Fragment,
                                  frameId: Int) {
            checkNotNull(fragmentManager)
            checkNotNull(fragment)
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(frameId, fragment)
            fragmentTransaction.commit()
        }
    }
}