package com.kk.gourmetapp.recommend

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.RequestBuilder
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop

interface RecommendContract {

    /**
     * Fragment側へ呼び出すUI関連の処理
     */
    interface View {
        fun setPresenter(recommendPresenter: RecommendPresenter)
        fun showRecognizeScreen()
        fun showGurunaviShops(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)
        fun showGurunaviTitle(keyword: String?, isCelebMode: Boolean)
        fun hideGurunaviTitle()
        fun showHotpepperShops(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?)
        fun showHotpepperTitle(keyword: String?, isCelebMode: Boolean)
        fun hideHotpepperTitle()
        fun showNetworkErrorDialog()
        fun showRequestLocationPermission()
        fun closeLocationLoadingFragment()
        fun showCelebBackground()
        fun removeCelebBackground()
        fun showApiCreditTransition(uri: Uri)
    }

    /**
     * Presenterのインターフェース
     */
    interface Presenter {
        fun createGurunaviInfo(bundle: Bundle)
        fun createHotpepperInfo(bundle: Bundle)
        fun loadGurunaviCredit(): RequestBuilder<Drawable>?
        fun loadHotpepperCredit(): RequestBuilder<Drawable>?
        fun updateGurunaviCreditTransition()
        fun updateHotpepperCreditTransition()
        fun shouldUpdate(): Boolean
        fun loadShopInfo()
        fun getSavedCurrentLocation(): Bundle
        fun researchShopManualKeyword(keyword: String)
        fun updateCelebMode()
    }
}