package com.kk.gourmetapp.recommend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kk.gourmetapp.R

class RecommendFragment : Fragment(), RecommendContract.View {

    fun RecommendFragment() {
        // do nothing.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }
}
