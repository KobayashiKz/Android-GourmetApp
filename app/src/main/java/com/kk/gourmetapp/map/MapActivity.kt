package com.kk.gourmetapp.map

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.kk.gourmetapp.R
import com.kk.gourmetapp.setting.SettingActivity
import com.kk.gourmetapp.util.ActivityUtil
import com.kk.gourmetapp.util.GoogleAnalyticsUtil

class MapActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    private lateinit var mPresenter: MapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Toolbarの設定
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        // ハンバーガーのメニュー画像を設置
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // ドロワーナビゲーションの設定
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        setupDrawerContent(navigationView)

        // Fragmentの生成
        val mapFragment: MapFragment = MapFragment.newInstance()
        ActivityUtil.addFragmentToActivity(supportFragmentManager, mapFragment,
            R.id.map_content_frame)

        // Presenterの生成
        mPresenter = MapPresenter(mapFragment, applicationContext)
    }

    override fun onResume() {
        super.onResume()

        GoogleAnalyticsUtil.sendScreenEvent(applicationContext,
            GoogleAnalyticsUtil.ScreenEvent.SHOW_MAP_SCREEN.key)
    }

    /**
     * ドロワーナビゲーションのアイテムがタップされたときの処理
     * @param navigationView
     */
    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_setting -> {
                    // 設定画面を起動する
                    val intent = Intent(applicationContext, SettingActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    // do nothing.
                }
            }
            // ドロワーのアイテムをタップされたときは必ずドロワーを閉じる
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                // ハンバーガー画像をタップされたらドロワーナビゲーションを表示
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
