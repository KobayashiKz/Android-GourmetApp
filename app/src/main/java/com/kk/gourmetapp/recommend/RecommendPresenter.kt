package com.kk.gourmetapp.recommend

import android.content.Context
import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop
import com.kk.gourmetapp.data.source.DataRepository
import com.kk.gourmetapp.data.source.DataSource

class RecommendPresenter(recommendView: RecommendContract.View, context: Context)
    : RecommendContract.UserActionListener {

    // フラグメントはコンストラクタで受け取る
    private var mRecommendView: RecommendContract.View = recommendView
    // レポジトリはコンストラクタで受け取る
    private var mDataRepository: DataRepository? = DataRepository(context)

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
        mDataRepository?.createGurunaviInfo(object :DataSource.CreateGurunaviShopCallback {
            override fun createGurunaviShop(
                shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
                // ぐるなびのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showGurunaviShops(shops, imageLoader)
            }
        })
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo() {
        mDataRepository?.createHotpepperInfo(object :DataSource.CreateHotpepperShopCallback {
            override fun createHotpepperShop(
                shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
                // ホットペッパーのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showHotpepperShops(shops, imageLoader)
                // 画像解析で取得保存しておいたキーワードを空文字にしておく
                mDataRepository?.removeRecognizeKeyword()
            }
        })
    }
}