package com.example.cappsule.database

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import android.database.Cursor

const val TABLE_OUTFIT_NAME = "outfit"
const val TABLE_COLUMN_NAME = "name"
const val TABLE_COLUMN_LAYER = "layer"
const val TABLE_COLUMN_TOP = "top"
const val TABLE_COLUMN_BOTTOM = "bottom"
const val TABLE_COLUMN_SHOES = "shoes"
const val TABLE_COLUMN_OUTFIT_ID = "ID"

class DatabaseHelperOutfit(context: Context?) : SQLiteOpenHelper(context, TABLE_OUTFIT_NAME, null, 8) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + TABLE_OUTFIT_NAME + " (" + TABLE_COLUMN_NAME + " TEXT, " + TABLE_COLUMN_LAYER + " BLOB, " +
                TABLE_COLUMN_TOP + " BLOB, " + TABLE_COLUMN_BOTTOM + " BLOB, " + TABLE_COLUMN_SHOES + " BLOB, " + TABLE_COLUMN_OUTFIT_ID + " INTEGER PRIMARY KEY)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OUTFIT_NAME")
        onCreate(db)
    }

    fun addOutfit(name: String?, layer: ByteArray?, top: ByteArray?, bottom: ByteArray?, shoes: ByteArray?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TABLE_COLUMN_NAME, name)
        contentValues.put(TABLE_COLUMN_LAYER, layer)
        contentValues.put(TABLE_COLUMN_TOP, top)
        contentValues.put(TABLE_COLUMN_BOTTOM, bottom)
        contentValues.put(TABLE_COLUMN_SHOES, shoes)
        db.insert(TABLE_OUTFIT_NAME, null, contentValues)
    }

    fun deleteOutfit(position: Int?) {
        val db = this.writableDatabase
        db.delete(TABLE_OUTFIT_NAME, "ID = ?", arrayOf((position!!).toString()))
    }

    fun updateName(position: Int?, name: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TABLE_COLUMN_NAME, name)
        db.update(TABLE_OUTFIT_NAME, values, "ID = ?", arrayOf((position!!).toString()))
    }

    val data: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM $TABLE_OUTFIT_NAME"
            return db.rawQuery(query, null)
        }
}