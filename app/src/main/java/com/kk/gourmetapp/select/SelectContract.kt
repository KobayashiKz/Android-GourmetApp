package com.kk.gourmetapp.select

import android.net.Uri

interface SelectContract {

    interface View {
        /**
         * Presenterの登録
         * @param selectPresenter 登録するPresenter
         */
        fun setUserActionListener(selectPresenter: SelectPresenter)
    }

    interface UserActionListener {

        /**
         * 画像認証処理
         * @param uri 画像認証する対象の画像Uri
         */
        fun startRecognizerImage(uri: Uri?)
    }
}