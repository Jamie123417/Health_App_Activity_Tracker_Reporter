package com.health_app_activity_tracker_reporter.resources

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.health_app_activity_tracker_reporter.classes.Tracker
import com.health_app_activity_tracker_reporter.classes.User

class DatabaseResources(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        if (!ifTableExists(db, TABLE_USERS)){
            try {
                db.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                        COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME + " TEXT, " +
                        COLUMN_USER_FIRST_NAME + " TEXT, " +
                        COLUMN_USER_LAST_NAME + " TEXT, " +
                        COLUMN_USER_EMAIL + " TEXT, " +
                        COLUMN_USER_PASSWORD + " TEXT " + ")")
            } catch (e: Exception){
            }
        }

        if (!ifTableExists(db, TABLE_TRACKERS)){
            try {
                db.execSQL("CREATE TABLE " + TABLE_TRACKERS + "(" +
                        COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_APP_NAME + " TEXT, " +
                        COLUMN_APP_PACKAGES + " TEXT, " +
                        COLUMN_APP_DLU + " INTEGER, " +
                        COLUMN_APP_WEEKS + " INTEGER, " +
                        COLUMN_APP_DAYS + " INTEGER, " +
                        COLUMN_APP_HOURS + " INTEGER " + ")")
            } catch (e: Exception){

            }
        }
        return
    }

    private fun ifTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        try {
            val cursor = db.query(tableName, null, null, null, null, null, null)
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.close()
                    return true
                }
                cursor.close()
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val deleteUserTable = "DROP TABLE IF EXISTS " + TABLE_USERS
        db.execSQL(deleteUserTable)
        val deleteTrackersTable = "DROP TABLE IF EXISTS " + TABLE_TRACKERS
        db.execSQL(deleteTrackersTable)
        onCreate(db)
        db.close()
    }

// Users Table Functions
    fun addAdmin() {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_USERNAME, "admin")
        cv.put(COLUMN_USER_FIRST_NAME, "John")
        cv.put(COLUMN_USER_LAST_NAME, "Smith")
        cv.put(COLUMN_USER_EMAIL, "admin@email.com")
        cv.put(COLUMN_USER_PASSWORD, "password1")
        db.insert(TABLE_USERS, null, cv)
        db.close()
    }

    fun addUser(user: User) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.userName)
        values.put(COLUMN_USER_FIRST_NAME, user.firstName)
        values.put(COLUMN_USER_LAST_NAME, user.lastName)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun updateUser(user: User) {
        val values = ContentValues()
        values.put(COLUMN_USER_ID, user.id)
        values.put(COLUMN_USERNAME, user.userName)
        values.put(COLUMN_USER_FIRST_NAME, user.firstName)
        values.put(COLUMN_USER_LAST_NAME, user.lastName)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        val db = this.writableDatabase
        db.update(TABLE_USERS,values, COLUMN_USER_ID + " = ?",arrayOf(java.lang.String.valueOf(user.id)))
        db.close()
    }
    fun deleteUser(user: User) {
        val db = this.writableDatabase
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", arrayOf(java.lang.String.valueOf(user.id)))
        db.close()
    }

    fun registerCheckUserEmail(email: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun loginCheckUserName(userName: String, password: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(userName, password)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun loginCheckUserEmail(email: String, password: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun getUsersdbSize(): Int {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_USER_ID)
        val selector = db.query(TABLE_USERS, columns,null, null, null,  null, null)
        val size = selector.columnCount
        selector.close()
        db.close()
        return size
    }

    fun getUserDetailsUserName(userName: String): User? {
        val query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = '" + userName + "'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val firstName = cursor.getString(2)
            val lastName = cursor.getString(3)
            val email = cursor.getString(4)
            val password = cursor.getString(4)
            user = User(id, name, firstName, lastName, email, password)
            cursor.close()
        }
        db.close()
        return user
    }

    fun getUserDetailsEmail(email: String): User? {
        val query = ("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "'")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val firstName = cursor.getString(2)
            val lastName = cursor.getString(3)
            val userEmail = cursor.getString(4)
            val password = cursor.getString(4)
            user = User(id, name, firstName, lastName, userEmail, password)
            cursor.close()
        }
        db.close()
        return user
    }

// Trackers Table Functions
    fun addTracker(tracker: Tracker) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_APP_NAME, tracker.appTName)
        values.put(COLUMN_APP_PACKAGES, tracker.appTPackages)
        values.put(COLUMN_APP_DLU, tracker.appTDateLastUsed)
        values.put(COLUMN_APP_WEEKS, tracker.appWeeks)
        values.put(COLUMN_APP_DAYS, tracker.appDays)
        values.put(COLUMN_APP_HOURS, tracker.appHours)
        db.insert(TABLE_TRACKERS, null, values)
        db.close()
    }

    fun deleteTracker(tracker: Tracker) {
        val db = this.writableDatabase
        db.delete(TABLE_TRACKERS, COLUMN_APP_ID + " = ?", arrayOf(java.lang.String.valueOf(tracker.trackID)))
        db.close()
    }

    fun updateTracker(tracker: Tracker) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_APP_NAME, tracker.appTName)
        values.put(COLUMN_APP_PACKAGES, tracker.appTPackages)
        values.put(COLUMN_APP_DLU, tracker.appTDateLastUsed)
        values.put(COLUMN_APP_WEEKS, tracker.appWeeks)
        values.put(COLUMN_APP_DAYS, tracker.appDays)
        values.put(COLUMN_APP_HOURS, tracker.appHours)
        db.update(TABLE_TRACKERS,values, COLUMN_APP_ID + " = ?",arrayOf(java.lang.String.valueOf(tracker.trackID)))
        db.close()
    }

    fun checkAppTracking(appName: String): Boolean {
        val columns = arrayOf(COLUMN_APP_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_APP_NAME = ?"
        val selectionArgs = arrayOf(appName)
        val cursor = db.query(TABLE_TRACKERS, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return (cursorCount > 0)
    }

    fun getAppTracker(appName: String): Tracker {
        val query = "SELECT * FROM " + TABLE_TRACKERS + " WHERE " + COLUMN_APP_NAME + " = '" + appName + "'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var tracker: Tracker = Tracker(0, "", "", 0, 0, 0, 0)
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            val appId = Integer.parseInt(cursor.getString(0))
            val appTName = cursor.getString(1)
            val appTPackages = cursor.getString(2)
            val appTDateLastUsed = cursor.getString(3).toLong()
            val appWeeks = cursor.getString(4).toInt()
            val appDays = cursor.getString(5).toInt()
            val appHours = cursor.getString(6).toInt()
            tracker = Tracker(appId, appTName, appTPackages, appTDateLastUsed, appWeeks, appDays, appHours)
            cursor.close()
        }
        db.close()
        return tracker
    }

    fun getAllTrackers(): MutableList<Tracker> {
        val trackerList: MutableList<Tracker> = ArrayList()
        // array of columns to fetch
        val columns = arrayOf(COLUMN_APP_ID, COLUMN_APP_NAME, COLUMN_APP_PACKAGES, COLUMN_APP_DLU, COLUMN_APP_WEEKS, COLUMN_APP_DAYS, COLUMN_APP_HOURS)
        // sorting orders
        val sortOrder = "$COLUMN_APP_NAME ASC"
        val db = this.readableDatabase
        // query the table
        val cursor = db.query(TABLE_TRACKERS, columns, null, null, null, null, sortOrder)
        if (cursor.moveToFirst()) {
            do {
                val tracker = Tracker(
                    trackID = cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID)).toInt(),
                    appTName = cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME)),
                    appTPackages = cursor.getString(cursor.getColumnIndex(COLUMN_APP_PACKAGES)),
                    appTDateLastUsed = cursor.getString(cursor.getColumnIndex(COLUMN_APP_DLU)).toLong(),
                    appWeeks = cursor.getString(cursor.getColumnIndex(COLUMN_APP_WEEKS)).toInt(),
                    appDays = cursor.getString(cursor.getColumnIndex(COLUMN_APP_DAYS)).toInt(),
                    appHours = cursor.getString(cursor.getColumnIndex(COLUMN_APP_HOURS)).toInt())
                trackerList.add(tracker)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return trackerList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ActivityTracker.db"
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USERNAME = "user_name"
        private const val COLUMN_USER_FIRST_NAME = "user_first_name"
        private const val COLUMN_USER_LAST_NAME = "user_last_name"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_USER_PASSWORD = "user_password"
        private const val TABLE_TRACKERS = "trackers"
        private const val COLUMN_APP_ID = "app_id"
        private const val COLUMN_APP_NAME = "app_name"
        private const val COLUMN_APP_PACKAGES = "app_packages"
        private const val COLUMN_APP_DLU = "app_date_last_used"
        private const val COLUMN_APP_WEEKS = "app_tracking_weeks"
        private const val COLUMN_APP_DAYS = "app_tracking_days"
        private const val COLUMN_APP_HOURS = "app_tracking_hours"
    }
}
