package com.kk.gourmetapp.recommend

import android.content.Context
import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.source.ShopDataSource
import com.kk.gourmetapp.data.source.ShopRepository

class RecommendPresenter(recommendView: RecommendContract.View, context: Context)
    : RecommendContract.UserActionListener {

    // フラグメントはコンストラクタで受け取る
    private var mRecommendView: RecommendContract.View = recommendView
    // レポジトリはコンストラクタで受け取る
    private var mShopRepository: ShopRepository? = ShopRepository(context)

    private var mContext: Context = context

    init {
        // FragmentにPresenterの登録要求
        mRecommendView.setUserActionListener(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo() {
        // Repository側でモデルクラス作成する
        mShopRepository?.createGurunaviInfo(object :ShopDataSource.CreateGurunaviShopCallback {
            override fun createdShop(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
                // ぐるなびのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showGurunaviShops(shops, imageLoader)
            }
        })
    }
}