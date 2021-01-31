package com.hasan.foraty.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hasan.foraty.criminalintent.model.Crime
import com.hasan.foraty.criminalintent.model.CrimeDetailViewModel
import java.util.*
private const val ART_CRIME_ID="crime_id"
class CrimeFragment private constructor() : Fragment() {

    private lateinit var crime:Crime
    private lateinit var titleField:EditText
    private lateinit var dateButton:Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel:CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    companion object{
        fun newInstance(id:UUID):CrimeFragment{
            val args=Bundle().apply {
                putSerializable(ART_CRIME_ID,id)
            }
            return CrimeFragment().apply {
                arguments=args
            }
        }
    }
    


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime=Crime()
        val crimeId:UUID=arguments?.getSerializable(ART_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime,container,false)

        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox=view.findViewById(R.id.crime_solved)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner,{crime ->
            titleField.setText(crime?.title)
            dateButton.text = crime?.date.toString()
            solvedCheckBox.isChecked= crime?.isSolved == true
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher=object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title=s.toString()
            }
            override fun afterTextChanged(s: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        dateButton.apply {
            text=crime.date.toString()
            isEnabled=false
        }
        solvedCheckBox.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}