package com.kk.gourmetapp.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kk.gourmetapp.R

class GoogleSignInUtil(context: Context) {

    var mContext: Context = context

    /**
     * サインインに使用するClientの生成処理
     * @return サインインClient
     */
    fun createGoogleSignInClient(): GoogleSignInClient {
        // GoogleAPIにログインしているかチェックする
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mContext.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(mContext, gso)
    }

    /**
     * ログインチェック
     * @return true: ログイン済み, false: 未ログイン
     */
    fun isGoogleSignIn(): Boolean {
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(mContext)
        return account != null
    }

    /**
     * Googleサインインする際のリクエストコード. StartActivityForResultのRequestコード
     */
    enum class GoogleSignInRequest{
        REQUEST_SIGN_IN;

        fun getCode(): Int {
            return ordinal
        }
    }
}