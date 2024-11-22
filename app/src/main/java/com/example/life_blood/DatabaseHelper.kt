package com.example.life_blood

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "LifeBlood.db"
        const val TABLE_DONORS = "donors"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_Password = "password"
        const val COLUMN_AGE = "age"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_CITY = "city"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_BLOOD_GROUP = "blood_group"
        private const val TABLE_SESSION = "user_session"
        private const val COLUMN_SESSION_ID = "session_id"
        private const val COLUMN_IS_LOGGED_IN = "is_logged_in"
        private const val COLUMN_SNAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_DONORS_TABLE = ("CREATE TABLE $TABLE_DONORS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_Password TEXT,"
                + "$COLUMN_AGE INTEGER,"
                + "$COLUMN_GENDER TEXT,"
                + "$COLUMN_PHONE TEXT,"
                + "$COLUMN_EMAIL TEXT,"
                + "$COLUMN_LATITUDE REAL,"
                + "$COLUMN_LONGITUDE REAL,"
                + "$COLUMN_CITY TEXT,"
                + "$COLUMN_COUNTRY TEXT,"
                + "$COLUMN_BLOOD_GROUP TEXT)")
        db?.execSQL(CREATE_DONORS_TABLE)
        val createSessionTable = (
                "CREATE TABLE $TABLE_SESSION ("
                    + "$COLUMN_SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_IS_LOGGED_IN INTEGER DEFAULT 0,"+ "$COLUMN_NAME TEXT)"
                )

        db?.execSQL(createSessionTable)
        val contentValues = ContentValues().apply { put(COLUMN_IS_LOGGED_IN, 0) }
        db?.insert(TABLE_SESSION, null, contentValues)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DONORS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SESSION")
        onCreate(db)
    }

    fun addDonor(donor: Donor): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, donor.name)
            put(COLUMN_Password, donor.password)
            put(COLUMN_AGE, donor.age)
            put(COLUMN_GENDER, donor.gender)
            put(COLUMN_PHONE, donor.phone)
            put(COLUMN_EMAIL, donor.email)
            put(COLUMN_LATITUDE, donor.latitude)
            put(COLUMN_LONGITUDE, donor.longitude)
            put(COLUMN_CITY, donor.city)
            put(COLUMN_COUNTRY, donor.country)
            put(COLUMN_BLOOD_GROUP, donor.bloodGroup)
        }
        return db.insert(TABLE_DONORS, null, values)
    }

    fun getDonorsByCityAndBloodGroup(city: String, bloodGroup: String): List<Donor> {
        val donors = mutableListOf<Donor>()
        val db = readableDatabase
        val cursor = db.rawQuery( "SELECT * FROM $TABLE_DONORS WHERE $COLUMN_CITY = ? AND $COLUMN_BLOOD_GROUP = ?", arrayOf(city, bloodGroup))

        if (cursor.moveToFirst()) {
            do {
                val donor = Donor(
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("age")),
                    cursor.getString(cursor.getColumnIndexOrThrow("gender")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
                    cursor.getString(cursor.getColumnIndexOrThrow("city")),
                    cursor.getString(cursor.getColumnIndexOrThrow("country")),
                    cursor.getString(cursor.getColumnIndexOrThrow("blood_group"))
                )
                donors.add(donor)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return donors
    }
    fun isDonorDuplicate(phone: String, email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_DONORS  WHERE $COLUMN_PHONE = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(phone, email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getDonorNameByIdentifier(identifier: String): String? {
        val db = readableDatabase
        var donorName: String? = null

        val query = "SELECT $COLUMN_NAME FROM $TABLE_DONORS WHERE $COLUMN_EMAIL = ? OR $COLUMN_PHONE = ?"
        val cursor = db.rawQuery(query, arrayOf(identifier, identifier))

        if (cursor.moveToFirst()) {
            donorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        }
        cursor.close()
        db.close()

        return donorName
    }

    fun validateLogin(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_DONORS WHERE ($COLUMN_EMAIL = ? OR $COLUMN_PHONE = ?) AND $COLUMN_Password = ?",
            arrayOf(email, email, password)

        )
        val exists = cursor.count > 0
        cursor.close()


        if (exists) {
            val donorName = getDonorNameByIdentifier(email)
            if (donorName != null) {
                setUserLoggedIn(donorName)
            }
        }

        cursor.close()
        db.close()
        return exists
    }

     fun setUserLoggedIn(name: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_LOGGED_IN, 1)
            put(COLUMN_SNAME, name)
        }
        db.update(TABLE_SESSION, values, null, null)
    }

    fun getLoggedInUserName(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NAME FROM $TABLE_SESSION WHERE $COLUMN_IS_LOGGED_IN = 1", null)
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        }
        cursor.close()
        db.close()
        return name
    }

    fun isUserLoggedIn(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IS_LOGGED_IN FROM $TABLE_SESSION ", null)
        var isLoggedIn = false
        if (cursor.moveToFirst()) {
            isLoggedIn = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_LOGGED_IN)) == 1
        }
        cursor.close()
        db.close()
        return isLoggedIn
    }
    fun logoutUser() {
        val db = writableDatabase
        try {
            val values = ContentValues().apply { put(COLUMN_IS_LOGGED_IN, 0) }
            val rowsUpdated = db.update(TABLE_SESSION, values, null, null)
            Log.d("Logout", if (rowsUpdated > 0) "User logged out successfully." else "No user found to log out.")
        } catch (e: Exception) {
            Log.e("Logout", "Error logging out user: ${e.message}")
        } finally {
            db.close()
        }
    }
}








