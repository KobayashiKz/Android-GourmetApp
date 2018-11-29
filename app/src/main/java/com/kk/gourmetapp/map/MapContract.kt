package com.kk.gourmetapp.map

interface MapContract {

    interface View {
        fun setPresenter(presenter: MapPresenter)
    }

    interface Presenter {
        fun getShopLatitude(): Double

        fun getShopLongitude(): Double

        fun clearShopAddressInfo()
    }

    interface Model {
        fun loadShopLatitude(): Double

        fun loadShopLongitude(): Double

        fun clearShopAddress()
    }
}