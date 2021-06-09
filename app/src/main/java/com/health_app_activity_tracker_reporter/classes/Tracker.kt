package com.health_app_activity_tracker_reporter.classes

data class Tracker(var trackID: Int = -1, var appTName: String, var appTPackages: String, var appTDateLastUsed: Long, var appWeeks: Int, var appDays: Int, var appHours: Int)