package com.example.health_app_activity_tracker_reporter

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
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
import java.util.Locale
import kotlin.collections.ArrayList


class TrackerActivity : AppCompatActivity() {

    private lateinit var listViewTrackedApps: ListView
    private lateinit var textViewTrackedAppsNo: TextView
    var trackedAppsList: MutableList<TrackerList> = ArrayList()
    private var appTrackerAdapter: AppTrackingListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(checkUsageStatsPermission()) {
            title = "Apps Currently Tracking"
            listViewTrackedApps = findViewById(R.id.tracked_app_list)
            textViewTrackedAppsNo = findViewById(R.id.appsTCounter)



        } else {
            startActivity(Intent(Settings.ACTION_APP_USAGE_SETTINGS))
        }
    }

    fun addTracker(newTracker: ArrayList<TrackerList>): Boolean {
        trackedAppsList.add(newTracker[1])
        return true
    }

    @Suppress("DEPRECATION")
    private fun checkUsageStatsPermission(): Boolean{
        var appOpsManager: AppOpsManager?
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        var mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return  mode == AppOpsManager.MODE_ALLOWED
    }

    class AppTrackingListAdapter(private val appCtx: Context, private val customizedList: MutableList<TrackerList>) : BaseAdapter() {
        private lateinit var icon: ImageView
        private lateinit var appName: TextView
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
            val view : View = LayoutInflater.from(appCtx).inflate(R.layout.custom_apps_tracking_layout, parent, false)
            icon = view.findViewById(R.id.app_t_icon)
            appName = view.findViewById(R.id.list_app_t_name)
            appDateLastUsed = view.findViewById(R.id.app_t_date_last_used)
            appUntilNotification = view.findViewById(R.id.app_t_time_notification)
            appNotificationLimit = view.findViewById(R.id.app_t_notification_limit)
            editTracker = view.findViewById(R.id.btn_T_Edit)
            deleteTracker = view.findViewById(R.id.btn_T_Delete)
            icon.setImageDrawable(customizedList[position].appIcon)
            appName.text = customizedList[position].appName
            appDateLastUsed.text = ("Last Time Used: " + convertTime(customizedList[position].dateLastUsed))
            appUntilNotification.text = ("Time Until " + appName + "needs to be used: "+ convertTime(timeUntilNotification(customizedList[position].dateLastUsed, customizedList[position].TrackingReportFreq)))
            appNotificationLimit.text = ("Frequency "+ appName + "needs to be used by: "+ customizedList[position].TrackingReportFreq)
            return view
        }

        private fun timeUntilNotification(dateLastUsed: Long, trackingReportFreq: Array<Int>): Long {
            val current : Long = System.currentTimeMillis()
            val timeGap = dateLastUsed - current
            val trackingReportFreqDays = trackingReportFreq[1].toString()
            val trackingReportHours = trackingReportFreq[2].toString()
            val trackingReportMinutes = trackingReportFreq[3].toString()

            val tDate : Date = Date(dateLastUsed)
            val tFormat = SimpleDateFormat("dd HH:mm", Locale.ENGLISH)
            val timeInString = (trackingReportFreqDays + trackingReportHours + ":" + trackingReportMinutes)
            val timeInDate = tFormat.parse(timeInString).time

            var timeUntilNotification = (timeGap - timeInDate)
            return timeUntilNotification
        }

        private fun convertTime(lastTimeUsed: Long): String {
            val date: Date = Date(lastTimeUsed)
            val format = SimpleDateFormat("dd/mm/yyyy HH:mm", Locale.ENGLISH)
            return format.format(date)
        }
    }
}