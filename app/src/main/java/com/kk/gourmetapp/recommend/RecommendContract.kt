package com.kk.gourmetapp.recommend

import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.data.GurunaviShop
import com.kk.gourmetapp.data.HotpepperShop

interface RecommendContract {

    /**
     * Fragment側へ呼び出すUI関連の処理
     */
    interface View {

        /**
         * Presenterの登録
         * @param recommendPresenter 登録するPresenter
         */
        fun setUserActionListener(recommendPresenter: RecommendPresenter)

        /**
         * ぐるなびのレストラン情報を表示
         * @param shops       レストランリスト
         * @param imageLoader レストラン画像
         */
        fun showGurunaviShops(shops: MutableList<GurunaviShop>, imageLoader: ImageLoader?)

        /**
         * ホットペッパーのレストラン情報を表示
         * @param shops       レストランリスト
         * @param imageLoader レストラン画像
         */
        fun showHotpepperShops(shops: MutableList<HotpepperShop>, imageLoader: ImageLoader?)
    }

    /**
     * Repository側へ呼び出すデータ関連の処理
     */
    interface UserActionListener {

        /**
         * ぐるなびのお店情報生成
         */
        fun createGurunaviInfo()

        /**
         * ホットペッパーのお店情報生成
         */
        fun createHotpepperInfo()

    }
}