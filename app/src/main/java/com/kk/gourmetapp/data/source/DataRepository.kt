package com.kk.gourmetapp.data.source

import android.content.Context
import android.net.Uri
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages
import com.kk.gourmetapp.data.ImageRecognizer
import com.kk.gourmetapp.data.source.remote.ShopRemoteRepository

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
    override fun startRecognizeImage(uri: Uri?, callback: DataSource.RecognizeCallback) {
        val imageRecognizer: ImageRecognizer = ImageRecognizer.newInstance(mContext)
        imageRecognizer.recognizeImage(uri, object : DataSource.RecognizeCallback {

            override fun onFinish(response: ClassifiedImages) {
                // 画像解析したデータをパースしてデータリストを生成
                val resultList: MutableList<ClassResult> = response.images[0].classifiers[0].classes
                val imageInfoList: MutableList<ImageInfo>? = ArrayList()
                for (result: ClassResult in resultList) {
                    val imageInfo: ImageInfo = ImageInfo(result.className, result.score, result.typeHierarchy)
                    imageInfoList?.add(imageInfo)
                }

                // 使用するキーワードを抽出する
                val keyword: String? = pickWord(imageInfoList)

                // キーワードが抽出できた場合だけコールバックを返す
                if (keyword != null) {
                    callback.onParsed(keyword)
                }
            }

            override fun onParsed(keyword: String?) {
                callback.onParsed(keyword)
            }
        })
    }

    /**
     * 画像解析結果から使用するキーワードを抜き出す
     * @param imageInfoList 画像解析結果リスト
     * @return キーワード
     */
    private fun pickWord(imageInfoList: MutableList<ImageInfo>?): String? {
        if (imageInfoList == null) return null

        var keyword: String? = null
        val score: Float = 0f
        // typeHierarchyをもっていないスコアのもっとも高いキーワードとして抽出する
        for (imageInfo: ImageInfo in imageInfoList) {
            if (imageInfo.getTypeHierarchy() == null) {
                if (score < imageInfo.getScore()) {
                    keyword = imageInfo.getName()
                }
            }
        }
        return keyword
    }

    /**
     * {@inheritDoc}
     */
    override fun saveRecognizeData() {
        // TODO: DBにキーワードを保存する
    }
}