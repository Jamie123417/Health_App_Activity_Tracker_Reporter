package com.health_app_activity_tracker_reporter

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.health_app_activity_tracker_reporter.R
import com.health_app_activity_tracker_reporter.classes.AppList
import java.text.SimpleDateFormat
import java.util.*

class AppsActivity : AppCompatActivity() {

    private lateinit var listViewUserApps: ListView
    private lateinit var textViewAppsNo: TextView
//    private lateinit var appAdapter: AppListAdapter
    private var installedAppsList: MutableList<AppList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(checkUsageStatsPermission()) {
            textViewAppsNo = findViewById(R.id.appsCounter)
            listViewUserApps = findViewById(R.id.installed_app_list)

            // get the list of installed apps
            installedAppsList = getInstalledApps()
            //Total Number of Installed-Apps(i.e. List Size)
            val userAppsNo = installedAppsList.count().toString() + ""
            textViewAppsNo.text = "Total User Installed Apps: $userAppsNo"

            //custom View for apps layout
//            appAdapter = AppListAdapter(this, installedAppsList)
            listViewUserApps.adapter = AppListAdapter(this, installedAppsList)
            listViewUserApps.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                Toast.makeText(this, installedAppsList[position].appPackages, Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        } else {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            Toast.makeText(this, "Please Enable Usage access for this app in Settings", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

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
                val dateLastUsed = getAppDateLastUsed(packages)
                appsList.add(AppList(appName, icon, packages, dateLastUsed))
            }
        }
        return appsList
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun getAppDateLastUsed(packageName: String): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        val usageStatsManager : UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val customUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, System.currentTimeMillis())
        for (i in customUsageStats.indices) {
            val pacName = customUsageStats[i].packageName.toString()
            if (packageName == pacName) {
                val dateLastUsed = customUsageStats[i].lastTimeUsed
                return (dateLastUsed)
            }
        }
        return 0
    }

    private fun checkUsageStatsPermission(): Boolean {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    private class AppListAdapter(private val appCtx: Context, private val customizedList: MutableList<AppList>) : BaseAdapter() {
        private lateinit var icon: ImageView
        private lateinit var appName: TextView
        private lateinit var packageName: TextView
        private lateinit var appDateLastUsed: TextView
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
            icon = view.findViewById(R.id.app_icon)
            appName = view.findViewById(R.id.list_app_name)
            packageName = view.findViewById(R.id.app_time_notification)
            appDateLastUsed = view.findViewById(R.id.app_date_last_used)
            icon.setImageDrawable(customizedList[position].appIcon)
            appName.text = customizedList[position].appName
            packageName.text = customizedList[position].appPackages
            appDateLastUsed.text = ("Time Last Used: " + convertTime(customizedList[position].dateLastUsed))
            return view
        }

        private fun convertTime(lastTimeUsed: Long): String {
            val date: Date = Date(lastTimeUsed)
            val formater = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
            return formater.format(date)
        }
    }

    fun getInstalledAppsList(): MutableList<AppList> {
        if (checkUsageStatsPermission()) {
            return getInstalledApps()
        }
        return ArrayList()
    }
}

