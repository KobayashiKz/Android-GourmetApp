package com.kk.gourmetapp.select

import android.net.Uri

interface SelectContract {

    interface View {
        /**
         * Presenterの登録
         * @param selectPresenter 登録するPresenter
         */
        fun setUserActionListener(selectPresenter: SelectPresenter)

        /**
         * 画像認証結果の表示
         * @param keyword 画像認証で抽出したキーワード
         */
        fun showRecognizeToast(keyword: String?)

        /**
         * 読み込み中アニメーションの停止
         */
        fun stopLoadingAnimation()
    }

    interface UserActionListener {
        /**
         * 画像認証処理
         * @param uri 画像認証する対象の画像Uri
         */
        fun startRecognizerImage(uri: Uri?)
    }
}