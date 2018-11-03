package com.kk.gourmetapp.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

class ActivityUtil {

    companion object {

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