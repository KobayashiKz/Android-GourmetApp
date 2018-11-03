package com.kk.todomvvmapp.gurumeapp.recommend

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.kk.todomvvmapp.gurumeapp.R
import com.kk.todomvvmapp.gurumeapp.data.source.ShopRepository
import com.kk.todomvvmapp.gurumeapp.setting.SettingActivity
import com.kk.todomvvmapp.gurumeapp.util.ActivityUtil

class RecommendActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

    private var mRecommendPresenter: RecommendPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

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
        val recommendFragment: RecommendFragment = RecommendFragment.newInstance()
        ActivityUtil.addFragmentToActivity(supportFragmentManager, recommendFragment,
            R.id.content_frame)

        // Presenterの生成
        mRecommendPresenter = RecommendPresenter(recommendFragment, ShopRepository())
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
            mDrawerLayout?.closeDrawers()
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
                mDrawerLayout?.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
