package com.hasan.foraty.criminalintent.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hasan.foraty.criminalintent.CrimeRepository
import java.io.File
import java.util.*

class CrimeDetailViewModel :ViewModel() {
    private val crimeRepository=CrimeRepository.get()
    private val crimeIdLiveData= MutableLiveData<UUID>()
    var crimeLiveData : LiveData<Crime?> =
            Transformations.switchMap(crimeIdLiveData)
            { crimeId ->
                crimeRepository.getCrime(crimeId)
            }
    fun loadCrime(crimeId:UUID){
        crimeIdLiveData.value=crimeId
    }
    fun saveCrime(crime:Crime){
        crimeRepository.updateCrime(crime)
    }
    fun deleteCrime(crime: Crime){
        crimeRepository.deleteCrime(crime)
    }
    fun getPhotoFile(crime: Crime):File=crimeRepository.getPhotoFile(crime)

}