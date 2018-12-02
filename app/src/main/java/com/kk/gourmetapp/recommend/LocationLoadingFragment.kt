package com.kk.gourmetapp.recommend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kk.gourmetapp.R

class LocationLoadingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_location_loading, container, false)

        // バックキーを押した際に終了させる
        root?.isFocusableInTouchMode = true
        root?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                removeLocationLoadingFragment()
            }
            false
        }
        return root
    }

    /**
     * 現在地読み込み中画面を閉じる
     */
    fun removeLocationLoadingFragment() {
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    companion object {
        fun newInstance(): LocationLoadingFragment {
            return LocationLoadingFragment()
        }
    }
}
