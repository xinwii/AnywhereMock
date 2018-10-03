package com.anywherecat.app.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


/**
 * @author xinwii
 * 数据库初始化
 */
class LocationDB(context: Context?, name: String?, version: Int) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table location_table(id integer primary key autoincrement, note varchar(100), address varchar(100),latitude varchar(20),longitude varchar(20))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}