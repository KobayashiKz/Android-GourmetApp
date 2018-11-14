package com.kk.gourmetapp.select

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.source.DataRepository
import com.kk.gourmetapp.data.source.DataSource

class SelectPresenter(fragment: SelectFragment, context: Context): SelectContract.UserActionListener {

    val mSelectView: SelectFragment = fragment
    val mContext: Context = context

    // UIスレッド
    val mHandler: Handler = Handler(Looper.getMainLooper())

    // レポジトリはコンストラクタで受け取る
    private var mDataRepository: DataRepository? = DataRepository(context)

    init {
        mSelectView.setUserActionListener(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun startRecognizerImage(uri: Uri?) {
        mDataRepository?.startRecognizeImage(uri, object: DataSource.RecognizeCallback {
            override fun onFinish(response: ClassifiedImages) {
                // do nothing.
            }
            override fun onParsed(keyword: String?) {
                mHandler.post {
                    mSelectView.stopLoadingAnimation()
                    mSelectView.showRecognizeToast(keyword)
                }
            }
        })
    }
}