package com.kk.gourmetapp.recommend

import android.content.Context
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.source.ShopDataSource
import com.kk.gourmetapp.data.source.ShopRepository

class RecommendPresenter(mRecomendView: RecommendContract.View, mContext: Context)
    : RecommendContract.UserActionListener {

    private var mShopRepository: ShopRepository? = null

    init {
        // FragmentにPresenterの登録要求
        mRecomendView.setUserActionListener(this)
        mShopRepository = ShopRepository(mContext)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo() {
        // Repository側でモデルクラス作成する
        mShopRepository?.createGurunaviInfo(object :ShopDataSource.CreateGurunaviShopCallback {
            override fun createdShop(shops: List<GurunaviShop>) {
                // TODO: ぐるなびのレストラン情報が取得できた場合にはFragmentにUI更新依頼をかける
            }
        })
    }
}