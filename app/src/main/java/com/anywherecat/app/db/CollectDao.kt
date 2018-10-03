package com.anywherecat.app.db

import android.content.Context
import com.anywherecat.app.model.CollectModel

/**
 * @author xinwii
 * dao
 */
class CollectDao {
    private var helper: LocationDB
    constructor(context:Context){
        helper = LocationDB(context, "location", 1)
    }
    fun insert(note:String,address:String,latitude:Double,longitude:Double){
        var db = helper.writableDatabase
        db.execSQL("insert into location_table(note,address,latitude,longitude) values(?,?,?,?)", arrayOf(note,address,latitude.toString(), longitude.toString()))
        db.close()
    }

    fun delete(id:Int){
        var db = helper.writableDatabase
        db.execSQL("delete from location_table where id=$id")
    }

    /**
     * 找出所有
     */
    fun findAll(): ArrayList<CollectModel> {
        val db = helper.writableDatabase
        val list = ArrayList<CollectModel>()
        val cursor = db.rawQuery("select * from location_table", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val note = cursor.getString(cursor.getColumnIndex("note"))
            val address = cursor.getString(cursor.getColumnIndex("address"))
            val latitude = cursor.getString(cursor.getColumnIndex("latitude"))
            val longitude = cursor.getString(cursor.getColumnIndex("longitude"))

            val userInfo = CollectModel(id, note, address, latitude.toDouble(), longitude.toDouble())
            list.add(userInfo)
        }
        cursor.close()
        db.close()
        return list
    }
}