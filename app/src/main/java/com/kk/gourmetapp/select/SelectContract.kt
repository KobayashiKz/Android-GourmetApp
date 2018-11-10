package com.kk.gourmetapp.select

import java.io.InputStream

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
         * @param inputStream 画像認証する対象の画像InputStream
         */
        fun startRecognizerImage(inputStream: InputStream?)
    }
}