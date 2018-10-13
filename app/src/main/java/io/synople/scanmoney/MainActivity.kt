package io.synople.scanmoney

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AWSMobileClient.getInstance().initialize(this) {
            Log.d("MainActivity", "AWSMobileClient is initialized")
        }.execute()

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, MainFragment.newInstance()).commit()
    }
}
