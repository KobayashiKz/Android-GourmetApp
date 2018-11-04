package com.kk.gourmetapp.data.source

import android.content.Context
import com.kk.gourmetapp.data.source.remote.ShopRemoteRepository

class ShopRepository(mContext: Context): ShopDataSource {

    private var mShopRemoteRepository: ShopRemoteRepository? = null

    init {
        mShopRemoteRepository = ShopRemoteRepository(mContext)
    }

    /**
     * {@inheritDoc}
     */
    override fun createGurunaviInfo(callback: ShopDataSource.CreateGurunaviShopCallback) {
        //リモート側でAPIをたたいて情報生成する
        mShopRemoteRepository?.createGurunaviInfo(callback)
    }
}