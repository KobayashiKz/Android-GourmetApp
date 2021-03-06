package com.kk.gourmetapp.recommend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import com.crashlytics.android.Crashlytics
import com.kk.gourmetapp.R
import com.kk.gourmetapp.setting.SettingActivity
import com.kk.gourmetapp.splash.SplashActivity
import com.kk.gourmetapp.util.ActivityUtil
import com.kk.gourmetapp.util.GoogleAnalyticsUtil
import io.fabric.sdk.android.Fabric

class RecommendActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

    private var mRecommendFragment: RecommendFragment = RecommendFragment.newInstance()
    private var mRecommendPresenter: RecommendPresenter? = null

    private lateinit var mKeywordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())

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
        ActivityUtil.addFragmentToActivity(supportFragmentManager, mRecommendFragment,
            R.id.content_frame)

        // Presenterの生成
        mRecommendPresenter = RecommendPresenter(mRecommendFragment, applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ActivityUtil.REQUEST_CODE_SPLASH && resultCode != Activity.RESULT_OK) {
            // SplashでAnimation完了しなかったら閉じる
            finish()
        } else if (requestCode == ActivityUtil.REQUEST_CODE_RECOGNIZE &&
            resultCode == Activity.RESULT_OK) {

            // 画像解析が完了している場合には取得済みの現在地で再検索をかける
            if (mRecommendPresenter?.shouldUpdate()!!) {
                val bundle: Bundle? = mRecommendPresenter?.getSavedCurrentLocation()
                if (bundle != null) {
                    mRecommendPresenter?.createGurunaviInfo(bundle)
                    mRecommendPresenter?.createHotpepperInfo(bundle)
                }
            }
        } else if (requestCode == ActivityUtil.REQUEST_CODE_SETTING &&
            resultCode == Activity.RESULT_OK) {
            // セレブモード変更した際には取得済みの現在地で再検索をかける
            val bundle: Bundle? = mRecommendPresenter?.getSavedCurrentLocation()
            if (bundle != null) {
                mRecommendPresenter?.createGurunaviInfo(bundle)
                mRecommendPresenter?.createHotpepperInfo(bundle)
            }

            // セレブモード背景の設定
            mRecommendPresenter?.updateCelebMode()
        }
    }

    override fun onResume() {
        super.onResume()

        GoogleAnalyticsUtil.sendScreenEvent(applicationContext,
            GoogleAnalyticsUtil.ScreenEvent.SHOW_RECOMMEND_SCREEN.key)
    }

    /**
     * ドロワーナビゲーションのアイテムがタップされたときの処理
     * @param navigationView
     */
    private fun setupDrawerContent(navigationView: NavigationView) {
        setupDrawerHeader(navigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_setting -> {
                    // 設定画面を起動する
                    val intent = Intent(applicationContext, SettingActivity::class.java)
                    startActivityForResult(intent, ActivityUtil.REQUEST_CODE_SETTING)

                    GoogleAnalyticsUtil.sendActionEvent(applicationContext,
                        GoogleAnalyticsUtil.ActionEventAction.CLICK_SETTINGS.key)
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
     * ナビゲーションドロワーヘッダーの初期セットアップ
     * @param navigationView ナビゲーションビュー
     */
    private fun setupDrawerHeader(navigationView: NavigationView) {
        val headerView: View = navigationView.getHeaderView(0)
        mKeywordEditText = headerView.findViewById(R.id.drawer_search_edit_text)

        val searchButton: ImageButton = headerView.findViewById(R.id.drawer_search_button)
        searchButton.setOnClickListener {
            // 入力したキーワードをもとに再検索
            val keyword: String = mKeywordEditText.text.toString()
            mKeywordEditText.editableText.clear()

            mRecommendPresenter?.researchShopManualKeyword(keyword)

            // ドロワーナビゲーションを閉じる
            mDrawerLayout?.closeDrawers()

            GoogleAnalyticsUtil.sendActionEvent(applicationContext,
                GoogleAnalyticsUtil.ActionEventAction.CLICK_SEARCH_BUTTON.key)
        }

        mKeywordEditText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            // エンターキーを押された場合
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // キーボードを閉じる
                closeKeyboard(mKeywordEditText)

                // 入力したキーワードをもとに再検索
                val keyword: String = mKeywordEditText.text.toString()
                mKeywordEditText.editableText.clear()

                mRecommendPresenter?.researchShopManualKeyword(keyword)

                // ドロワーナビゲーションを閉じる
                mDrawerLayout?.closeDrawers()

                return@OnKeyListener true
            }
            false
        })
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

    /**
     * ソフトキーボードを閉じる
     * @param editText エディットテキスト
     */
    private fun closeKeyboard(editText: EditText) {
        val inputMethodManager: InputMethodManager
                =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN)

    }

    companion object {
        const val TAG: String = "RecommendActivity"
    }
}
