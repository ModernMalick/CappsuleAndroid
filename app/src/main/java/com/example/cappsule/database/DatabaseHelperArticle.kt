package com.example.cappsule.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cappsule.R

const val TABLE_ARTICLE_NAME = "article"
const val TABLE_COLUMN_IMAGE = "image"
const val TABLE_COLUMN_AVAILABLE = "available"
const val TABLE_COLUMN_TYPE = "type"
const val TABLE_COLUMN_WARMTH = "warmth"
const val TABLE_COLUMN_ARTICLE_ID = "ID"

class DatabaseHelperArticle(context: Context?) : SQLiteOpenHelper(context, TABLE_ARTICLE_NAME, null, 13) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_ARTICLE_NAME + " (" + TABLE_COLUMN_IMAGE + " BLOB, " +
                TABLE_COLUMN_AVAILABLE + " INTEGER, " + TABLE_COLUMN_TYPE + " TEXT, " + TABLE_COLUMN_WARMTH + " TEXT, "
                + TABLE_COLUMN_ARTICLE_ID + " INTEGER PRIMARY KEY)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTICLE_NAME")
        onCreate(db)
    }

    fun addArticle(image: ByteArray?, integer: Int?, context: Context) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TABLE_COLUMN_IMAGE, image)
        contentValues.put(TABLE_COLUMN_AVAILABLE, integer)
        contentValues.put(TABLE_COLUMN_TYPE, context.getString(R.string.type_not_set))
        contentValues.put(TABLE_COLUMN_WARMTH, context.getString(R.string.warmth_unset))
        db.insert(TABLE_ARTICLE_NAME, null, contentValues)
    }

    fun deleteArticle(position: Int?) {
        val db = this.writableDatabase
        db.delete(TABLE_ARTICLE_NAME, "ID = ?", arrayOf((position!!).toString()))
    }

    fun updateAvailability(position: Int?, availability: Int?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TABLE_COLUMN_AVAILABLE, availability)
        db.update(TABLE_ARTICLE_NAME, values, "ID = ?", arrayOf((position!!).toString()))
    }

    fun updateType(position: Int?, type: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TABLE_COLUMN_TYPE, type)
        db.update(TABLE_ARTICLE_NAME, values, "ID = ?", arrayOf((position!!).toString()))
    }

    fun updateWarmth(position: Int?, warmth: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TABLE_COLUMN_WARMTH, warmth)
        db.update(TABLE_ARTICLE_NAME, values, "ID = ?", arrayOf((position!!).toString()))
    }

    val data: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM $TABLE_ARTICLE_NAME"
            return db.rawQuery(query, null)
        }
}