package com.example.health_app_activity_tracker_reporter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.collections.ArrayList


class TrackerActivity : AppCompatActivity() {

    private lateinit var listViewTrackedApps: ListView
    private lateinit var textViewTrackedAppsNo: TextView
    private lateinit var databaseResources: DatabaseResources
    private lateinit var appListInstance: AppsActivity
    private var trackedAppsList: MutableList<Tracker> = ArrayList()
    private var installedApps: MutableList<AppList> = ArrayList()
    private var appTrackerAdapter: AppTrackingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appListInstance = AppsActivity()

        if(appListInstance.checkUsageStatsPermission()) {
            databaseResources = DatabaseResources(applicationContext)
            listViewTrackedApps = findViewById(R.id.tracked_app_list)
            textViewTrackedAppsNo = findViewById(R.id.appsTCounter)
        } else {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            Toast.makeText(this, "Please Enable Usage access for this app in Settings", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun getTrackers(): MutableList<Tracker>  {
        return databaseResources.getAllTrackers()
    }

    fun getAppIcon(trackedAppName: String): Drawable {
        installedApps = appListInstance.getInstalledApps()
        try {
            for (i in installedApps.indices) {
                if (trackedAppName == installedApps[i].appName)
                    return installedApps[i].appIcon
            }
        } catch (e: Exception) {
        }
        return null as Drawable
    }

    class AppTrackingListAdapter(private val appCtx: Context, private val customized: MutableList<Tracker>) : BaseAdapter() {
        private lateinit var icon: ImageView
        private lateinit var appName: TextView
        private lateinit var appDateLastUsed: TextView
        private lateinit var appUntilNotification: TextView
        private lateinit var appNotificationLimit: TextView
        private lateinit var editTracker: Button
        private lateinit var deleteTracker: Button
        override fun getCount(): Int {
            return customized.size
        }
        override fun getItem(position: Int): Any {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view : View = LayoutInflater.from(appCtx).inflate(R.layout.custom_apps_tracking_layout, parent, false)
            icon = view.findViewById(R.id.app_t_icon)
            appName = view.findViewById(R.id.list_app_t_name)
            appDateLastUsed = view.findViewById(R.id.app_t_date_last_used)
            appUntilNotification = view.findViewById(R.id.app_t_time_notification)
            appNotificationLimit = view.findViewById(R.id.app_t_notification_limit)
            editTracker = view.findViewById(R.id.btn_T_Edit)
            deleteTracker = view.findViewById(R.id.btn_T_Delete)
//            icon.setImageDrawable(customized[position].appIcon)
            appName.text = customized[position].appTName
            appDateLastUsed.text = ("Last Time Used: " + convertTime(customized[position].appTdateLastUsed))
//            appUntilNotification.text = ("Time Until " + appName + "needs to be used: "+ convertTime(timeUntilNotification(customized[position].dateLastUsed, customized[position].trackingInterval)))
            appNotificationLimit.text = ("Frequency "+ appName + "needs to be used by: "+ customized[position].appTrackingInterval)
            return view
        }

/*        private fun timeUntilNotification(dateLastUsed: Long, trackingReportFreq: Array<Int>): Long {
            val current : Long = System.currentTimeMillis()
            val timeGap = dateLastUsed - current
            val trackingReportFreqDays = trackingReportFreq[1].toString()
            val trackingReportHours = trackingReportFreq[2].toString()
            val trackingReportMinutes = trackingReportFreq[3].toString()

            val tDate : Date = Date(dateLastUsed)
            val tFormat = SimpleDateFormat("dd HH:mm", Locale.ENGLISH)
            val timeInString = (trackingReportFreqDays + trackingReportHours + ":" + trackingReportMinutes)
            val timeInDate = tFormat.parse(timeInString).time
*//*            val dt = Date()
            LocalDateTime.from(dt.toInstant()).plusDays(1)*//*
            var timeUntilNotification = (timeGap - timeInDate)
            return timeUntilNotification
        }*/

        private fun convertTime(lastTimeUsed: Long): String {
            val date: Date = Date(lastTimeUsed)
            val format = SimpleDateFormat("dd/mm/yyyy HH:mm", Locale.ENGLISH)
            return format.format(date)
        }
    }
}