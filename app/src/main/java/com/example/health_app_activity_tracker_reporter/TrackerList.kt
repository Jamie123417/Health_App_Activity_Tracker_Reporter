package com.example.health_app_activity_tracker_reporter

import android.graphics.drawable.Drawable

//object for each apps variables

class TrackerList(var appName: String, var appIcon: Drawable, var appPackages: String, var dateLastUsed: Long, var TrackingReportFreq: Array<Int>)