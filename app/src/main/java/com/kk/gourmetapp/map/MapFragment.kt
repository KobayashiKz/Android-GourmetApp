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

    private lateinit var mMap: GoogleMap

    private lateinit var mPresenter: MapPresenter

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

        val latitude: Double = mPresenter.getGurunaviLatitude()
        val longitude: Double = mPresenter.getGurunaviLongitude()

        mPresenter.clearGurunaviAddressInfo()

        if (latitude == 0.0 && longitude == 0.0) {
            // TODO: 住所取得できなかった際の処理
            return
        }

        val location = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))

        mMap.addMarker(
            MarkerOptions()
                .position(location)
        )

        val shopLocation: CameraPosition = CameraPosition.Builder().
            target(location)
            .zoom(15.5f)
            .bearing(0f)
            .tilt(25f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(shopLocation))

        if (hasLocationPermission()) {
            mMap.isMyLocationEnabled = true
        } else {
            showRequestPermission()
        }
    }

    // パーミッションダイアログ表示
    private fun showRequestPermission() {
        EasyPermissions.requestPermissions(this, getString(R.string.location_request_permission_message),
            REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // パーミッションチェック
    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun setPresenter(presenter: MapPresenter) {
        mPresenter = presenter
    }

    companion object {
        // パーミッションリクエストコード
        const val REQUEST_CODE_LOCATION = 1

        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}
