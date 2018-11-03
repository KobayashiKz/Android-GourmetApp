package com.kk.gourmetapp.recommend

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
    }

    /**
     * Repository側へ呼び出すデータ関連の処理
     */
    interface UserActionListener {

        /**
         * ぐるなびのお店情報生成
         */
        fun createGurunaviInfo()
    }
}