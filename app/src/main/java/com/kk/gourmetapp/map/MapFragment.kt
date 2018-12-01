package com.kk.gourmetapp.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kk.gourmetapp.R
import pub.devrel.easypermissions.EasyPermissions

class MapFragment : Fragment(), MapContract.View, OnMapReadyCallback {

    private lateinit var mPresenter: MapPresenter

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root: View? = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment: SupportMapFragment = childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        // 緯度経度を取得
        val latitude: Double = mPresenter.getShopLatitude()
        val longitude: Double = mPresenter.getShopLongitude()

        mPresenter.clearShopAddressInfo()

        if (latitude == 0.0 && longitude == 0.0) {
            // TODO: 住所取得できなかった際の処理
            return
        }
        val location = LatLng(latitude, longitude)

        // 対象のショップが表示されるようにMapに各種設定
        mMap.addMarker(MarkerOptions().position(location))
        val shopLocation: CameraPosition = CameraPosition.Builder().
            target(location)
            .zoom(15.5f)     // 拡大度
            .bearing(0f)     // 北方向向き
            .tilt(25f)       // 角度
            .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(shopLocation))

        if (hasLocationPermission()) {
            mMap.isMyLocationEnabled = true
        } else {
            showRequestPermission()
        }
    }

    /**
     * Presenterの登録
     */
    override fun setPresenter(presenter: MapPresenter) {
        mPresenter = presenter
    }

    /**
     * 現在地パーミッションの表示
     */
    private fun showRequestPermission() {
        EasyPermissions.requestPermissions(this, getString(R.string.location_request_permission_message),
            REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * 現在地パーミッションのチェック
     * @return true:  取得済み
     *         false: 未取得
     */
    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    companion object {
        // パーミッションリクエストコード
        const val REQUEST_CODE_LOCATION = 1

        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}
