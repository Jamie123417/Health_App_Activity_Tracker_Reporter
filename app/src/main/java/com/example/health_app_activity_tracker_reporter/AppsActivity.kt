package com.example.health_app_activity_tracker_reporter

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AppsActivity : AppCompatActivity() {

    private lateinit var listViewUserApps: ListView
    private lateinit var textViewAppsNo: TextView
    private var installedApps: MutableList<AppList> = ArrayList()
    var appAdapter: AppListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Installed Apps"
        textViewAppsNo = findViewById(R.id.appsCounter)
        listViewUserApps = findViewById(R.id.installed_app_list)

        // get the list of installed apps
        installedApps = getInstalledApps()
        //Total Number of Installed-Apps(i.e. List Size)
        val userAppsNo = installedApps.count().toString() + ""
        textViewAppsNo.text = "Total User Installed Apps: $userAppsNo"

        //custom View for apps layout
        appAdapter = AppListAdapter(this, installedApps)
        listViewUserApps.adapter = AppListAdapter(this, installedApps)
        listViewUserApps.setOnItemClickListener{parent, view, position, id ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + installedApps[position].appPackages)
            Toast.makeText(this, installedApps[position].appPackages, Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    //object for each apps variables
    class AppList(var appName: String, var appIcon: Drawable, var appPackages: String)

    //fills array on installed apps
    private fun getInstalledApps(): MutableList<AppList> {
        var appsList: MutableList<AppList> = ArrayList()
        val appListPacks: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in appListPacks.indices) {
            val packageInfo = appListPacks[i]
            if (!isSystemPackage(packageInfo)) {
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                val icon = packageInfo.applicationInfo.loadIcon(packageManager)
                val packages = packageInfo.applicationInfo.packageName
                appsList.add(AppList(appName, icon, packages))
            }
        }
        return appsList
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    class AppListAdapter(private val appCtx: Context, private val customizedList: MutableList<AppList>) : BaseAdapter() {
        private lateinit var Icon: ImageView
        private lateinit var appName: TextView
        private lateinit var packageName: TextView
        override fun getCount(): Int {
            return customizedList.size
        }
        override fun getItem(position: Int): Any {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view : View = LayoutInflater.from(appCtx).inflate(R.layout.custom_apps_layout, parent, false)
            Icon = view.findViewById(R.id.app_icon)
            appName = view.findViewById(R.id.list_app_name)
            packageName = view.findViewById(R.id.app_package)
            Icon.setImageDrawable(customizedList[position].appIcon)
            appName.text = customizedList[position].appName
            packageName.text = customizedList[position].appPackages
            return view
        }
    }
}

