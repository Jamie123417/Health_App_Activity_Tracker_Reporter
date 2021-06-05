package com.example.health_app_activity_tracker_reporter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*
import com.example.health_app_activity_tracker_reporter.User


class DatabaseResources(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
/*        val createUserTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_USER_FIRST_NAME + " TEXT, " +
                COLUMN_USER_LAST_NAME + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_USER_PASSWORD + " TEXT " + ")"*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME + " TEXT, " + COLUMN_USER_FIRST_NAME + " TEXT, " + COLUMN_USER_LAST_NAME + " TEXT, " + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT " + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val deleteUserTable = "DROP TABLE IF EXISTS " + TABLE_USER
        db.execSQL(deleteUserTable)
        onCreate(db)
        db.close()
    }

    fun addAdmin() {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_USERNAME, "admin")
        cv.put(COLUMN_USER_FIRST_NAME, "john")
        cv.put(COLUMN_USER_LAST_NAME, "smith")
        cv.put(COLUMN_USER_EMAIL, "admin@email.com")
        cv.put(COLUMN_USER_PASSWORD, "password")
        db.insert(TABLE_USER, null, cv)
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
        db.insert(TABLE_USER, null, values)
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
        db.update(TABLE_USER,values,COLUMN_USER_ID + " = ?",arrayOf(java.lang.String.valueOf(user.id)))
        db.close()
    }
    fun deleteUser(user: User) {
        val delUser = this.writableDatabase
        delUser.delete(TABLE_USER, COLUMN_USER_ID + " = ?", arrayOf(java.lang.String.valueOf(user.id)))
        delUser.close()
    }

    fun registerCheckUser(email: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(
            TABLE_USER, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun loginCheckUser(userName: String, password: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(userName, password)
        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs,null,null,null)
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
        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount > 0
    }

    fun getDBSize(): Int {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_USER_ID)
        val selector = db.query(TABLE_USER, columns,null, null, null,  null, null)
        val size = selector.columnCount
        selector.close()
        db.close()
        return size
    }

    fun findUserDetailsName(userName: String): User? {
        val query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + " = '" + userName + "'"
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

    fun findUserDetailsEmail(email: String): User {
        val query = ("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = '" + email + "'")
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

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserManager.db"
        private const val TABLE_USER = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USERNAME = "user_name"
        private const val COLUMN_USER_FIRST_NAME = "user_first_name"
        private const val COLUMN_USER_LAST_NAME = "user_last_name"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_USER_PASSWORD = "user_password"
    }
}
