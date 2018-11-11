package com.kk.gourmetapp.select

import android.content.Context
import android.net.Uri
import com.kk.gourmetapp.data.source.DataRepository
import java.io.InputStream

class SelectPresenter(fragment: SelectFragment, context: Context): SelectContract.UserActionListener {

    val mSelectView: SelectFragment = fragment
    val mContext: Context = context

    // レポジトリはコンストラクタで受け取る
    private var mDataRepository: DataRepository? = DataRepository(context)


    init {
        mSelectView.setUserActionListener(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun startRecognizerImage(uri: Uri?) {
        mDataRepository?.startRecognizeImage(uri)
    }
}