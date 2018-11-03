package com.kk.gourmetapp.recommend

interface RecommendContract {

    /**
     * Fragment側へ呼び出すUI関連の処理
     */
    interface View {
        // Presenterの登録処理
        fun setUserActionListener(recommendPresenter: RecommendPresenter)



    }

    /**
     * Repository側へ呼び出すデータ関連の処理
     */
    interface UserActionListener {
        // 通信してデータを取得する
        fun createGurunaviInfo()
    }
}