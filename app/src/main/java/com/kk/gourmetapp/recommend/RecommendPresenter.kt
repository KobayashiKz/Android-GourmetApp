package com.kk.gourmetapp.recommend

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
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
    override fun createGurunaviInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository?.isCelebMode()

            // Repository側でモデルクラス作成する
            mDataRepository?.createGurunaviInfo(object : DataSource.CreateGurunaviShopCallback {
                override fun createGurunaviShop(
                    shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?) {
                    // ぐるなびのレストラン情報が取得できた場合にはUI更新をかける
                    mRecommendView.showGurunaviShops(shops, imageLoader)
                }

                override fun onError() {
                    // 結果が取得できなかったら通信状況確認ダイアログ表示
                    if (!isConnectNetwork()) {
                        mRecommendView.showNetworkErrorDialog()
                    }
                }
            }, isCelebMode as Boolean , bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun createHotpepperInfo(bundle: Bundle) {

        val isCelebMode: Boolean? = mDataRepository?.isCelebMode()

        mDataRepository?.createHotpepperInfo(object :DataSource.CreateHotpepperShopCallback {
            override fun createHotpepperShop(
                shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?) {
                // ホットペッパーのレストラン情報が取得できた場合にはUI更新をかける
                mRecommendView.showHotpepperShops(shops, imageLoader)
                // 画像解析で取得保存しておいたキーワードを空文字にしておく
                mDataRepository?.removeRecognizeKeyword()
            }

            override fun onError() {
                // 結果が取得できなかったら通信状況確認ダイアログ表示
                if (!isConnectNetwork()) {
                    mRecommendView.showNetworkErrorDialog()
                }
            }
        }, isCelebMode as Boolean, bundle)
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCredit(): RequestBuilder<Drawable>? {
        return mDataRepository?.loadGurunaviCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCredit(): RequestBuilder<Drawable>? {
        return mDataRepository?.loadHotpepperCredit()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadGurunaviCreditUri(): Uri? {
        return mDataRepository?.loadGurunaviCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun loadHotpepperCreditUri(): Uri? {
        return mDataRepository?.loadHotpepperCreditUri()
    }

    /**
     * {@inheritDoc}
     */
    override fun isCelebMode(): Boolean {
        return mDataRepository?.isCelebMode() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    override fun shouldUpdate(): Boolean  {
        return mDataRepository?.shouldUpdate() as Boolean
    }

    /**
     * {@inheritDoc}
     */
    override fun isConnectNetwork(): Boolean {
        return mDataRepository?.isConnectNetwork() as Boolean
    }

    override fun loadShopInfo() {
        if (mDataRepository!!.hasLocationPermission()) {
            // パーミッションが許可されている場合には現在地を取得する
            mDataRepository?.getCurrentLocation(object : DataSource.LocationCallback {
                override fun onComplete(bundle: Bundle) {
                    // 現在地取得後にショップ情報を作成
                    createGurunaviInfo(bundle)
                    createHotpepperInfo(bundle)
                }
            })
        } else {
            // パーミッションが許可されていない場合にはダイアログ表示させる
            mRecommendView.showRequestLocationPermission()
        }
    }

    // 現在地取得できたらコールバックを取得する. その中でショップインフォを作成する
}