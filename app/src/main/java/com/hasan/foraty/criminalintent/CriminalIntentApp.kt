package com.hasan.foraty.criminalintent

import android.app.Application

class CriminalIntentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}