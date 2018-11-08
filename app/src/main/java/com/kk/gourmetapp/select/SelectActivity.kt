package com.kk.gourmetapp.select

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kk.gourmetapp.R
import com.kk.gourmetapp.util.ActivityUtil

class SelectActivity : AppCompatActivity() {

    var mSelectPresenter: SelectPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        // Fragmentの生成
        val selectFragment: SelectFragment = SelectFragment.newInstance()
        ActivityUtil.addFragmentToActivity(supportFragmentManager, selectFragment, R.id.select_content_frame)

        // Presenterの生成
        mSelectPresenter = SelectPresenter(selectFragment, applicationContext)
    }
}
