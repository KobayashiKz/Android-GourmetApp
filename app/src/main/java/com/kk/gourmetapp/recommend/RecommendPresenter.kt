package com.kk.gourmetapp.recommend

import com.kk.gourmetapp.data.source.ShopRepository

class RecommendPresenter(mRecomendView: RecommendContract.View)
    : RecommendContract.UserActionListener {

    private var mShopRepository: ShopRepository? = null

    init {
        // FragmentにPresenterの登録要求
        mRecomendView.setUserActionListener(this)
        mShopRepository = ShopRepository()
    }

    /**
     * ぐるなびのお店情報を生成する処理
     */
    override fun createGurunaviInfo() {
        // Repository側でモデルクラス作成する
        mShopRepository?.createGurunaviInfo()
    }
}