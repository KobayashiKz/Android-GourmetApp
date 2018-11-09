package com.kk.gourmetapp.select

import android.content.Context

class SelectPresenter(fragment: SelectFragment, context: Context): SelectContract.UserActionListener {

    val mSelectView: SelectFragment = fragment
    val mContext: Context = context

    init {
        mSelectView.setUserActionListener(this)
    }
}