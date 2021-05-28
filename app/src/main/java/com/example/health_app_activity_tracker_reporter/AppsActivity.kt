package com.example.health_app_activity_tracker_reporter

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
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

        if(checkUsageStatsPermission()) {
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
            listViewUserApps.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + installedApps[position].appPackages)
                Toast.makeText(this, installedApps[position].appPackages, Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }else {
                startActivity(Intent(Settings.ACTION_APP_USAGE_SETTINGS))
        }
    }

    //object for each apps variables
    class AppList(var appName: String, var appIcon: Drawable, var appPackages: String, var dateLastUsed: String)

    private fun getInstalledApps(): MutableList<AppList> {
        //fills array on installed apps
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

    private fun getAppDateLastUsed(packageName: String): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        val usageStatsManager : UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        var customUsageStats = usageStatsManager.queryAndAggregateUsageStats(cal.timeInMillis, System.currentTimeMillis())
        val customUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, System.currentTimeMillis())
        for (i in customUsageStats.indices) {
            val pacName = customUsageStats[i].packageName.toString()
            if (packageName == pacName) {
                val dateLastUsed = customUsageStats[i].lastTimeUsed
                return ("Last Time Used " + convertTime(dateLastUsed))
            }
        }
        return ""
    }

    private fun convertTime(lastTimeUsed: Long): String {
        val date: Date = Date(lastTimeUsed)
        val format = SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    @Suppress("DEPRECATION")
    private fun checkUsageStatsPermission(): Boolean{
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return  mode == AppOpsManager.MODE_ALLOWED
    }

    class AppListAdapter(private val appCtx: Context, private val customizedList: MutableList<AppList>) : BaseAdapter() {
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
            packageName = view.findViewById(R.id.app_package)
            appDateLastUsed = view.findViewById(R.id.app_date_last_used)
            icon.setImageDrawable(customizedList[position].appIcon)
            appName.text = customizedList[position].appName
            packageName.text = customizedList[position].appPackages
            appDateLastUsed.text = customizedList[position].dateLastUsed
            return view
        }
    }
}

