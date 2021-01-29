package com.hasan.foraty.criminalintent.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hasan.foraty.criminalintent.model.Crime

@Database(entities = [Crime::class],version = 1)
@TypeConverters(CrimeTypeConverter::class)
abstract class CrimeDatabase :RoomDatabase(){
    abstract fun crimeDao():CrimeDao
}
