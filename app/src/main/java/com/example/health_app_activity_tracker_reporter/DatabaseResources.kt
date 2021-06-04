package com.example.health_app_activity_tracker_reporter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


class DatabaseResources (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val createUserTable = ("CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")")
    private val deleteUserTable = "DROP TABLE IF EXISTS " + TABLE_USER

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(createUserTable)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL(deleteUserTable)
        onCreate(database)
    }

    fun addUser(user: User) {
        val database = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.userName)
        values.put(COLUMN_USER_FIRST_NAME, user.firstName)
        values.put(COLUMN_USER_LAST_NAME, user.lastName)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        database.insert(TABLE_USER, null, values)
        database.close()
    }

    val getAllUsers: List<User>
        get() {
            val columns = arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_FIRST_NAME, COLUMN_USER_LAST_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD)
            // sorting
            val sort = COLUMN_USER_NAME + " ASC"
            val userList: MutableList<User> = ArrayList()
            val database = this.readableDatabase
            // query
            val selector = database.query(TABLE_USER, columns,null, null, null,  null,sort)
            if (selector.moveToFirst()) {
                do {
                    val user = User(0, "temp", "temp", "temp", "temp", "temp")
                    user.id = selector.getString(selector.getColumnIndex(COLUMN_USER_ID)).toInt()
                    user.userName = selector.getString(selector.getColumnIndex(COLUMN_USER_NAME))
                    user.email = selector.getString(selector.getColumnIndex(COLUMN_USER_EMAIL))
                    user.password = selector.getString(selector.getColumnIndex(COLUMN_USER_PASSWORD))
                    userList.add(user)
                } while (selector.moveToNext())
            }
            selector.close()
            database.close()
            return userList
        }

    fun updateUser(user: User) {
        val database = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.userName)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)
        database.update(TABLE_USER,values,COLUMN_USER_ID + " = ?",arrayOf(java.lang.String.valueOf(user.id)))
        database.close()
    }

    fun deleteUser(user: User) {
        val delUser = this.writableDatabase
        delUser.delete(TABLE_USER, COLUMN_USER_ID + " = ?", arrayOf(java.lang.String.valueOf(user.id)))
        delUser.close()
    }

    fun checkUser(email: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val dataBase = this.readableDatabase
        val selection = COLUMN_USER_EMAIL + " = ?"
        val selectionArgs = arrayOf(email)
        val cursor = dataBase.query(TABLE_USER, columns, selection, selectionArgs,null,null,null)
        val cursorCount = cursor.count
        cursor.close()
        dataBase.close()
        return cursorCount > 0
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserManager.database"
        private const val TABLE_USER = "user"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USER_NAME = "user_name"
        private const val COLUMN_USER_FIRST_NAME = "user_first_name"
        private const val COLUMN_USER_LAST_NAME = "user_Last_name"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_USER_PASSWORD = "user_password"
    }

    fun getDBSize(): Int {
        val database = this.readableDatabase
        val columns = arrayOf(COLUMN_USER_ID, COLUMN_USER_EMAIL, COLUMN_USER_NAME, COLUMN_USER_PASSWORD)
        val sort = COLUMN_USER_NAME + " ASC"
        val selector = database.query(TABLE_USER, columns,null, null, null,  null,sort)
        selector.close()
        database.close()
        return selector.columnCount
    }

    fun getUserDetails(userEmail: String): User {
        var userDetails = User(0, "temp", "temp", "temp", "temp", "temp")
        val database = this.readableDatabase

        val query = "SELECT * FROM " + TABLE_USER.toString() + " WHERE " + COLUMN_USER_EMAIL.toString() + " = '" + userEmail.toString() + "'"
        val cursor: Cursor = database.rawQuery(query, null)
//        val cursor = database.query(TABLE_USER, null, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                userDetails.id = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
                userDetails.userName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME))
                userDetails.email = userEmail
                userDetails.firstName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME))
                userDetails.lastName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME))
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()
        return userDetails
    }

}
