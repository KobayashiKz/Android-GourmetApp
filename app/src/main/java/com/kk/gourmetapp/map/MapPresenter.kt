package com.kk.gourmetapp.map

import android.content.Context

class MapPresenter(mapView: MapContract.View, context: Context): MapContract.Presenter {

    private var mView: MapContract.View = mapView
    private var mModel: MapContract.Model = MapModel(context)

    init {
        mView.setPresenter(this)
    }

    /**
     * ぐるなびショップの緯度取得
     * @return 緯度
     */
    override fun getShopLatitude(): Double {
        return mModel.loadShopLatitude()
    }

    /**
     * ぐるなびショップの経度取得
     * @return 経度
     */
    override fun getShopLongitude(): Double {
        return mModel.loadShopLongitude()
    }

    /**
     * ぐるなびショップの位置情報の削除.
     * 位置情報取得用に一時保存しているため.
     */
    override fun clearShopAddressInfo() {
        mModel.clearShopAddress()
    }
}