package com.kk.gourmetapp.recommend

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kk.gourmetapp.R
import com.kk.gourmetapp.setting.SettingActivity
import com.kk.gourmetapp.util.ActivityUtil
import com.kk.gourmetapp.util.GoogleSignInUtil



class RecommendActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null

    private var mRecommendPresenter: RecommendPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        setupGoogleAccount()
    }

    /**
     * Googleアカウントにログインしていない場合にはログイン画面を起動する
     */
    private fun setupGoogleAccount() {
        val googleSignInUtil: GoogleSignInUtil = GoogleSignInUtil(this)
        if (!googleSignInUtil.isGoogleSignIn()) {
            // ログインしていない場合はサインイン画面を起動する
            val googleSignInClient: GoogleSignInClient = googleSignInUtil.createGoogleSignInClient()
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GoogleSignInUtil.GoogleSignInRequest.REQUEST_SIGN_IN.getCode())
        } else {
            // すでにログイン済みの場合はログイン処理をスキップ
            setupActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInUtil.GoogleSignInRequest.REQUEST_SIGN_IN.getCode()) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // サインインできた場合にFirebaseの認証を行う
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Failed to Google SignIn: " + e.message)
            }
        }
    }

    /**
     * Firebaseのログイン認証
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential : AuthCredential = GoogleAuthProvider.getCredential(account?.idToken, null)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { p0 ->
                if (p0.isSuccessful) {
                    // サインイン認証成功したらUIをUI更新する
                    // Firebase認証を行ったことのないユーザーは自動的に作成されるため現在のUser情報が取得できる
                    val user: FirebaseUser? = mAuth.currentUser
                    setupActivity()
                } else {
                    // サインイン失敗時
                    Log.w(TAG, "Failed to Firebase auth: " + p0.exception)
                }
            }
    }

    /**
     * Activityの初期セットアップ
     */
    private fun setupActivity() {
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

    companion object {
        val TAG: String = "RecommendActivity"
    }
}
