package com.kk.gourmetapp.data.source

import com.kk.gourmetapp.data.source.remote.ShopRemoteRepository

class ShopRepository: ShopDataSource {

    private var mShopRemoteRepository: ShopRemoteRepository? = null

    init {
        mShopRemoteRepository = ShopRemoteRepository()
    }

    /**
     * ぐるなびのお店情報を生成する処理
     */
    override fun createGurunaviInfo() {
        //リモート側でAPIをたたいて情報生成する
        mShopRemoteRepository?.createGurunaviInfo()
    }
}