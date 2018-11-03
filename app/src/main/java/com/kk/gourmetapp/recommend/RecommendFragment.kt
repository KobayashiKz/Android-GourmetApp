package com.kk.gourmetapp.recommend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kk.gourmetapp.R

class RecommendFragment : Fragment(), RecommendContract.View {

    var mRecommendPresenter: RecommendPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // ぐるなびのお店情報を取得開始
        mRecommendPresenter?.createGurunaviInfo()

        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }

    /**
     * {@inheritDoc}
     */
    override fun setUserActionListener(recommendPresenter: RecommendPresenter) {
        mRecommendPresenter = recommendPresenter
    }

    companion object {
        fun newInstance(): RecommendFragment {
            return RecommendFragment()
        }
    }
}
