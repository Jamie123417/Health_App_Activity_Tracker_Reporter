package com.example.health_app_activity_tracker_reporter

import android.graphics.drawable.Drawable

data class Tracker(var trackID: Int, var appName: String, var appPackages: String, var dateLastUsed: Long, var trackingInterval: Int)