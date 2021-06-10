package com.health_app_activity_tracker_reporter

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.health_app_activity_tracker_reporter.R
import com.google.android.material.snackbar.Snackbar
import com.health_app_activity_tracker_reporter.classes.AppList
import com.health_app_activity_tracker_reporter.classes.Tracker
import com.health_app_activity_tracker_reporter.classes.Trackers
import com.health_app_activity_tracker_reporter.resources.DatabaseResources
import com.health_app_activity_tracker_reporter.resources.NotificationPublisher
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class TrackerActivity : AppCompatActivity() {
    private lateinit var listViewTrackedApps: ListView
    private lateinit var textViewTrackedAppsNo: TextView
    private lateinit var databaseResources: DatabaseResources
    private var trackedList: MutableList<Trackers> = ArrayList()
    private var trackedAppsData: MutableList<Tracker> = ArrayList()
    private var userInsAppsList: MutableList<AppList> = ArrayList()
//    private val notification_channel_id = "activityTrackers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(checkUsageStatsPermission()) {
            userInsAppsList = getInstalledApps()
            databaseResources = DatabaseResources(applicationContext)
            listViewTrackedApps = findViewById(R.id.tracked_app_list)
            textViewTrackedAppsNo = findViewById(R.id.appsTCounter)

            trackedAppsData = databaseResources.getAllTrackers()
            for (i in trackedAppsData.indices) {
                val appTrName = trackedAppsData[i].appTName
                val appTrPackages = trackedAppsData[i].appTPackages
                var appTrIcon: Drawable = ColorDrawable(Color.TRANSPARENT)
                for (j in userInsAppsList.indices) {
                    val pComp = userInsAppsList[j].appPackages
                    if (appTrPackages == pComp) {
                        appTrIcon = userInsAppsList[j].appIcon
                        break
                    }
                }
                val appTrDateLastUsed = getAppDateLastUsed(appTrPackages)
                val appTrWeeks = trackedAppsData[i].appWeeks
                val appTrDays = trackedAppsData[i].appDays
                val appTrHours = trackedAppsData[i].appHours
                trackedList.add(Trackers(appTrName, appTrPackages, appTrIcon, appTrDateLastUsed, appTrWeeks, appTrDays, appTrHours))
            }
            textViewTrackedAppsNo.text = ("Total Number of Tracked Apps: " + trackedList.count().toString() + "")

            for (n in trackedList.indices){
                try {
                    cancelNotifications(n, trackedList[n].appTrName)
                    val dateOfAlert = dateOfNotification(trackedList[n].appTrDateLastUsed, trackedList[n].appTrWeeks, trackedList[n].appTrDays, trackedList[n].appTrHours)
                    scheduleNotification(getNotification(trackedList[n].appTrName, n), dateOfAlert, n)
                } catch (e: Exception){

                }
            }

            //custom View for layout
            listViewTrackedApps.adapter = AppTrackingListAdapter(this, trackedList, databaseResources)
/*            listViewTrackedApps.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                Toast.makeText(this, trackedList[position].appTrPackages, Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }*/
            listViewTrackedApps.setOnClickListener {
                val intent = intent
                finish()
                startActivity(intent)
            }
        } else {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            Toast.makeText(this, "Please Enable Usage access for this app in Settings", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    private fun scheduleNotification(notification: Notification, dateOfNotif: Date, notNum: Int) {
        val notificationIntent = Intent(this, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notNum)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val futureInMillis = SystemClock.elapsedRealtime() + dateOfNotif.time
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
        alarmManager.set(AlarmManager.RTC_WAKEUP, convertDateToCalendar(dateOfNotif).timeInMillis, pendingIntent)

    }
    private fun getNotification(content: String, j: Int): Notification {
        val notificationMessage : String = ("Remember to use " + content + " as you have not used it recently")
        val NOTIFICATION_CHANNEL_ID = (j + 10000).toString()
        var notification =  Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_activity_tracker_round)
            .setContentTitle("Notification for " + content)
            .setContentText(notificationMessage)
//            .addAction(action)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        return notification
    }
    fun cancelNotifications(id: Int, tag: String) {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(tag, (id+10000))
    }

    private fun convertLongToTime(lastTimeUsed: Long): String {
        val date: Date = Date(lastTimeUsed)
        val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
        return formater.format(date)
    }
    fun convertDateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }
    private fun dateOfNotification(dateAppLastUsed: Long, weeks: Int, days: Int, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = (Date(dateAppLastUsed))
        calendar.add(Calendar.DATE, (weeks * 7))
        calendar.add(Calendar.DATE, days)
        calendar.add(Calendar.HOUR, hours)
        return calendar.time
    }
/*    private fun dayDifference(dateToCheck: Date): String {
        val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
        val currentDate: Date = formater.parse(convertLongToTime(System.currentTimeMillis()))
        val difference: Long = kotlin.math.abs(currentDate.time - dateToCheck.time)
        val differenceDates = difference / (24 * 60 * 60 * 1000)
        return differenceDates.toString()
    }*/

    //fills array on installed apps
    private fun getInstalledApps(): MutableList<AppList> {
        var appsList: MutableList<AppList> = ArrayList<AppList>()
        val appPacks: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        for (i in appPacks.indices) {
            val packageNo = appPacks[i]
            if (isSystemPackage(packageNo)) {
                val newInfo = AppList("", ColorDrawable(Color.TRANSPARENT), "", 0)
                newInfo.appName = packageNo.applicationInfo.loadLabel(packageManager).toString()
                newInfo.appIcon = packageNo.applicationInfo.loadIcon(packageManager)
                newInfo.appPackages = packageNo.applicationInfo.packageName
                newInfo.dateLastUsed = getAppDateLastUsed(newInfo.appPackages)
                appsList.add(newInfo)
            }
        }
        return appsList
    }
    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return (pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 1
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
        return System.currentTimeMillis()
    }
    private fun checkUsageStatsPermission(): Boolean {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    class AppTrackingListAdapter(private val appCtx: Context, private val customizedList: MutableList<Trackers>, private var databaseResources: DatabaseResources) : BaseAdapter() {
        private lateinit var icon: ImageView
        private lateinit var appName: TextView
        private lateinit var appDateLastUsed: TextView
        private lateinit var appUntilNotification: TextView
        private lateinit var appNotificationLimit: TextView
        private lateinit var btnEditTracker: Button
        private lateinit var btnDeleteTracker: Button
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
            val view: View = LayoutInflater.from(appCtx).inflate(R.layout.custom_apps_tracking_layout, parent, false)
            icon = view.findViewById(R.id.app_t_icon)
            appName = view.findViewById(R.id.list_app_t_name)
            appDateLastUsed = view.findViewById(R.id.app_t_date_last_used)
            appUntilNotification = view.findViewById(R.id.app_t_time_notification)
            appNotificationLimit = view.findViewById(R.id.app_t_notification_limit)
            btnEditTracker = view.findViewById(R.id.btn_t_Edit)
            btnDeleteTracker = view.findViewById(R.id.btn_t_Delete)
            icon.setImageDrawable(customizedList[position].appTrIcon)
            appName.text = customizedList[position].appTrName
            appDateLastUsed.text =("Time App Last Used: " + convertLongToTimeAdapter(customizedList[position].appTrDateLastUsed))
            appUntilNotification.text = ("Time Until " + customizedList[position].appTrName + " needs to be used: "+ convertDateToStringAdapter(dateOfNotificationAdapter(customizedList[position].appTrDateLastUsed, customizedList[position].appTrWeeks, customizedList[position].appTrDays, customizedList[position].appTrHours)))
            appNotificationLimit.text = ("Frequency "+ customizedList[position].appTrName + " needs to be used by: " + customizedList[position].appTrWeeks + " weeks, " + customizedList[position].appTrDays + " days, "  + customizedList[position].appTrHours + " hours")
            btnDeleteTracker.setOnClickListener  {
                databaseResources.deleteTracker(databaseResources.getAppTracker(customizedList[position].appTrName))
            }
/*            btnEditTracker.setOnClickListener {
                val intent = Intent(this, EditTrackerActivity::class.java)
                intent.putExtra("APPNAME", customizedList[position].appTrName)
                startActivity(intent)
            }*/
            return view
        }
        private fun convertLongToTimeAdapter(lastTimeUsed: Long): String {
            val date: Date = Date(lastTimeUsed)
            val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
            return formater.format(date)
        }
        private fun convertDateToStringAdapter(date: Date) : String {
            val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
            return formater.format(date)
        }
        private fun dateOfNotificationAdapter(dateAppLastUsed: Long, weeks: Int, days: Int, hours: Int ): Date {
            val date: Date = Date(dateAppLastUsed)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DATE, (weeks*7))
            calendar.add(Calendar.DATE, days)
            calendar.add(Calendar.HOUR, hours)
            val datePlusTracker: Date = calendar.time
            return datePlusTracker
        }
    }
}
