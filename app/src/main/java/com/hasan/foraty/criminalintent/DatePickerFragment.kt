package com.hasan.foraty.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*
private const val ARG_DATE="date"
class DatePickerFragment:DialogFragment() {

    interface Callbacks{
        fun onDateSelected(date: Date)
    }
    companion object{
        fun newInstance(date:Date):DatePickerFragment{
            val arg=Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return DatePickerFragment().apply {
                arguments=arg
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener=DatePickerDialog.OnDateSetListener{
            datePicker: DatePicker, year: Int, month: Int, day: Int ->
            val resultDate=GregorianCalendar(year,month,day).time
            targetFragment?.let {
                (it as Callbacks).onDateSelected(resultDate)
            }
        }
        val arg=arguments?.getSerializable(ARG_DATE) as Date

        val calendar=Calendar.getInstance()
        calendar.time=arg
        val initializeYear=calendar.get(Calendar.YEAR)
        val initializeMonth=calendar.get(Calendar.MONTH)
        val initializeDay=calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
                requireContext(),
                dateListener,
                initializeYear,
                initializeMonth,
                initializeDay)
    }

}