package com.kk.gourmetapp.select

interface SelectContract {

    interface View {
        /**
         * Presenterの登録
         * @param selectPresenter 登録するPresenter
         */
        fun setUserActionListener(selectPresenter: SelectPresenter)
    }

    interface UserActionListener {
    }
}