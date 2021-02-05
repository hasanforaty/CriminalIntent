package com.hasan.foraty.criminalintent.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hasan.foraty.criminalintent.model.Crime

@Database(entities = [Crime::class],version = 2)
@TypeConverters(CrimeTypeConverter::class)
abstract class CrimeDatabase :RoomDatabase(){
    abstract fun crimeDao():CrimeDao
}
val migration_1_2=object :Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
                "ALTER Table Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}
