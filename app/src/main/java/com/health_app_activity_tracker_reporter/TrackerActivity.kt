package com.health_app_activity_tracker_reporter

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.health_app_activity_tracker_reporter.R
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
    private lateinit var appListInstance: AppsActivity
    private var trackedAppsData: MutableList<Tracker> = ArrayList()
    private var trackedList: MutableList<Trackers> = ArrayList()
    private var userInsAppsList: MutableList<AppList> = ArrayList()
    val NOTIFICATION_CHANNEL_ID = "10001"
    private val notification_channel_id = "activityTrackers"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appListInstance = AppsActivity()
        userInsAppsList = getInstalledApps()

        if(checkUsageStatsPermission()) {
            databaseResources = DatabaseResources(applicationContext)
            listViewTrackedApps = findViewById(R.id.tracked_app_list)
            textViewTrackedAppsNo = findViewById(R.id.appsTCounter)

            trackedAppsData = databaseResources.getAllTrackers()
            for (i in trackedAppsData.indices) {
                trackedList[i].appTrName = trackedAppsData[i].appTName
                trackedList[i].appTrPackages = trackedAppsData[i].appTPackages
                for (j in userInsAppsList.indices) {
                    if (userInsAppsList[j].appPackages == trackedList[i].appTrPackages ) {
                        trackedList[i].appTrIcon = userInsAppsList[j].appIcon
                    }
                }
                trackedList[i].appTrDateLastUsed = getAppDateLastUsed(trackedList[i].appTrPackages)
                trackedList[i].appTrWeeks = trackedAppsData[i].appWeeks
                trackedList[i].appTrDays = trackedAppsData[i].appDays
                trackedList[i].appTrHours = trackedAppsData[i].appHours
            }
            textViewTrackedAppsNo.text = ("Total Number of Tracked Apps: " + trackedList.count().toString() + "")

            for (j in trackedList.indices){
                try {
                    cancelNotifications(j, trackedList[j].appTrName)
                    val dateOfAlert = dateOfNotification(trackedList[j].appTrDateLastUsed, trackedList[j].appTrWeeks, trackedList[j].appTrDays, trackedList[j].appTrHours)
                    scheduleNotification(getNotification(trackedList[j].appTrName), dateOfAlert, j)
                } catch (e: Exception){

                }
            }

            //custom View for layout
            listViewTrackedApps.adapter = AppTrackingListAdapter(this, trackedList)
            listViewTrackedApps.setOnItemClickListener { parent, view, position, id ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                Toast.makeText(this, trackedList[position].appTrPackages, Toast.LENGTH_SHORT).show()
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
    private fun getNotification(content: String): Notification {
        val notificationMessage : String = ("Remember to use " + content + " as you have not used it recently")
        var notification =  Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_activity_tracker_round)
            .setContentTitle("Notification for " + content)
            .setContentText(notificationMessage)
//            .addAction(action)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        return notification
    }
    fun cancelNotifications(id: Int, tag: String?) {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(tag, id)
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
    private fun dayDifference(dateToCheck: Date): String {
        val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
        val currentDate: Date = formater.parse(convertLongToTime(System.currentTimeMillis()))
        val difference: Long = kotlin.math.abs(currentDate.time - dateToCheck.time)
        val differenceDates = difference / (24 * 60 * 60 * 1000)
        return differenceDates.toString()
    }
    private fun getInstalledApps(): MutableList<AppList> {
        var appsList: MutableList<AppList> = java.util.ArrayList()
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    class AppTrackingListAdapter(private val appCtx: Context, private val customizedList: MutableList<Trackers>) : BaseAdapter() {
        private lateinit var icon: ImageView
        private lateinit var appName: TextView
        private lateinit var packageName: TextView
        private lateinit var appDateLastUsed: TextView
        private lateinit var appUntilNotification: TextView
        private lateinit var appNotificationLimit: TextView
        private lateinit var editTracker: Button
        private lateinit var deleteTracker: Button
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
            editTracker = view.findViewById(R.id.btn_T_Edit)
            deleteTracker = view.findViewById(R.id.btn_T_Delete)
            icon.setImageDrawable(customizedList[position].appTrIcon)
            appName.text = customizedList[position].appTrName
            packageName.text = customizedList[position].appTrPackages
            appDateLastUsed.text =("Time App Last Used: " + convertLongToTimeAdapter(customizedList[position].appTrDateLastUsed))
            appNotificationLimit.text = ("Frequency "+ appName + "needs to be used by: " + customizedList[position].appTrWeeks + " weeks, " + customizedList[position].appTrDays + " days, "  + customizedList[position].appTrHours + " hours")
            appUntilNotification.text = ("Time Until " + appName + "needs to be used: "+ convertDateToStringAdapter(dateOfNotificationAdapter(customizedList[position].appTrDateLastUsed, customizedList[position].appTrWeeks, customizedList[position].appTrDays, customizedList[position].appTrHours)))
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

/*        fun isTimeAlert(dateToCheck: Long): Long {
            val formater = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
//            cal.add(Calendar.YEAR, -1)
            val currentDate = System.currentTimeMillis()
            val date1: Date = formater.parse(convertLongToTime(currentDate))
            val date2: Date = formater.parse(convertLongToTime(dateToCheck))
            val difference: Long = kotlin.math.abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
//            val dayDifference = differenceDates.toString()
            return differenceDates
        }*/
    }
}
