package com.example.health_app_activity_tracker_reporter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class AppsActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var arrayAdapter: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        title = "KotlinApps"
        listView = findViewById(R.id.listView)
        installedApps()

    }

    private fun installedApps() {
        val appList = packageManager.getInstalledPackages(0)
        for (i in appList.indices) {
            val packageInfo = appList[i]
            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                Log.e("App List$i", appName)
                arrayAdapter = ArrayAdapter(
                    this,
                    R.layout.support_simple_spinner_dropdown_item, appList as List<*>
                )
                listView.adapter = arrayAdapter
            }
        }
    }
}
