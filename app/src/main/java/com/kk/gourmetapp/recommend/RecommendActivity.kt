package com.kk.gourmetapp.recommend

import android.app.Activity
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
import com.kk.gourmetapp.splash.SplashActivity
import com.kk.gourmetapp.util.ActivityUtil



class RecommendActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

    private var mRecommendPresenter: RecommendPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // スプラッシュ画面の起動
        val splashIntent = Intent(this, SplashActivity::class.java)
        startActivityForResult(splashIntent, ActivityUtil.REQUEST_CODE_SPLASH)

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
        mRecommendPresenter = RecommendPresenter(recommendFragment, applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ActivityUtil.REQUEST_CODE_SPLASH && resultCode != Activity.RESULT_OK) {
            // SplashでAnimation完了しなかったら閉じる
            finish()
        } else if (requestCode == ActivityUtil.REQUEST_CODE_RECOGNIZE &&
            resultCode == Activity.RESULT_OK) {

            // 画像解析が完了している場合には再検索をかける
            if (mRecommendPresenter?.shouldUpdate()!!) {
                mRecommendPresenter?.createGurunaviInfo()
                mRecommendPresenter?.createHotpepperInfo()
            }
        } else if (requestCode == ActivityUtil.REQUEST_CODE_SETTING &&
            resultCode == Activity.RESULT_OK) {
            // セレブモード変更した際には再検索をかける
            mRecommendPresenter?.createGurunaviInfo()
            mRecommendPresenter?.createHotpepperInfo()
        }
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
                    startActivityForResult(intent, ActivityUtil.REQUEST_CODE_SETTING)
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

    companion object {
        const val TAG: String = "RecommendActivity"
    }
}
