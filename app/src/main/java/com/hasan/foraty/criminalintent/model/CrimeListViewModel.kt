package com.hasan.foraty.criminalintent.model

import androidx.lifecycle.ViewModel
import com.hasan.foraty.criminalintent.CrimeRepository

class CrimeListViewModel : ViewModel() {
    private val crimeRepository =CrimeRepository.get()

    val crimeLiveList=crimeRepository.getCrimes()
}