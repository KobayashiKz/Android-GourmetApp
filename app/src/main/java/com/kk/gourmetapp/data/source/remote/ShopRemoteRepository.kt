package com.kk.gourmetapp.data.source.remote

import com.android.volley.toolbox.ImageLoader
import com.kk.gourmetapp.data.source.ShopDataSource

class ShopRemoteRepository: ShopDataSource {

    private var mImageLoader: ImageLoader? = null

    init {
        mImageLoader = RequestSingleQueue.getImageLoader()
    }

    /**
     * ぐるなびAPIをたたいて情報を取得する処理
     */
    override fun createGurunaviInfo() {
        // TODO: Volleyによる通信処理
//        val request: JsonObjectRequest = JsonObjectRequest("URL", null,
//            Response.Listener { response ->
//                // TODO: APIから情報取得成功した際の処理
//            },
//            Response.ErrorListener { error ->
//                // TODO: エラー時の処理
//            })
    }
}