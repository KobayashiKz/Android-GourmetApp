package com.kk.gourmetapp.data.source

import android.content.Context
import com.kk.gourmetapp.data.ImageRecognizer
import com.kk.gourmetapp.data.source.remote.ShopRemoteRepository
import java.io.InputStream

class DataRepository(context: Context): DataSource {

    private val mContext: Context = context

    private var mShopRemoteRepository: ShopRemoteRepository? = null

    init {
        mShopRemoteRepository = ShopRemoteRepository(mContext)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(callback: DataSource.CreateGurunaviShopCallback) {
        //リモート側でAPIをたたいて情報生成する
        mShopRemoteRepository?.createGurunaviInfo(callback)
    }

    /**
     * {@inheritDoc}
     */
    override fun startRecognizeImage(inputStream: InputStream?) {
        val imageRecognizer: ImageRecognizer = ImageRecognizer.newInstance(mContext)
        imageRecognizer.recognizeImage(inputStream, object : DataSource.RecognizeCallback {
            override fun onFinish() {
                // TODO: 受け取ったデータをパースする
            }
        })
    }

    /**
     * {@inheritDoc}
     */
    override fun saveRecognizeData() {
        // TODO: DBにキーワードを保存する
    }
}