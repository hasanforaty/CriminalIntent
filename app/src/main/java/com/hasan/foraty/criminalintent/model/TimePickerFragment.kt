package com.hasan.foraty.criminalintent.model

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.TimeUnit
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.sql.Time
import java.sql.Timestamp
import java.util.*

private const val ARG_DATE="DateArgs"
class TimePickerFragment:DialogFragment(),TimePickerDialog.OnTimeSetListener {

    companion object{
        fun newInstance(date:Date):TimePickerFragment{
            val arg=Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return TimePickerFragment().apply {
                arguments=arg
            }
        }
    }
    interface Callback{
        fun onTimeSelected(date: Date)
    }

    private lateinit var date:Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date=arguments?.getSerializable(ARG_DATE) as Date
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val initializeHoure= date.hours
        val initializeMinute=date.minutes
        return TimePickerDialog(
                requireContext(),
                this,
                initializeHoure,
                initializeMinute,
                true
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        targetFragment?.apply {
            date.hours=hourOfDay
            date.minutes=minute
            (this as Callback).onTimeSelected(date)
        }
    }
}