package com.hasan.foraty.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hasan.foraty.criminalintent.model.Crime
import java.util.*

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes():LiveData<List<Crime>>
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id:UUID):LiveData<Crime?>
    @Update
    fun updateCrime(crime: Crime)
    @Insert
    fun addCrime(crime:Crime)

}

