package com.hasan.foraty.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hasan.foraty.criminalintent.model.Crime
import com.hasan.foraty.criminalintent.model.CrimeListViewModel
import java.util.*

private const val TAG="CrimeListFragment"
class CrimeListFragment : Fragment() {
    /**
     * interface that need to be implemented in parent
     */
    interface Callbacks{
        fun onCrimeSelected(id:UUID)
    }

    private var callbacks:Callbacks?=null
    private lateinit var crimeRecycleView : RecyclerView
    private val diffUtilCallbacks= DiffUtilCallback()
    private var adapter : CrimeAdapter=CrimeAdapter(emptyList(),diffUtilCallbacks)
    private val crimeListViewModel:CrimeListViewModel by lazy{
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view=inflater.inflate(R.layout.fragment_crime_list,container,false)

        crimeRecycleView=view.findViewById(R.id.crime_recycle_view)
        crimeRecycleView.layoutManager=LinearLayoutManager(context)
        crimeRecycleView.adapter=adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeLiveList.observe(
                viewLifecycleOwner,
                { crimes->
                    crimes?.let {
                        updateUI(crimes)

                    }
                })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callbacks){
            callbacks=context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime ->{
                val crime=Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                if (crime.title.isEmpty()){

                }
                true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

    private inner class DiffUtilCallback: DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.title.equals(newItem.title, ignoreCase = true)
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem==newItem
        }

    }

    private inner class CrimeHolder(view:View):RecyclerView.ViewHolder(view),View.OnClickListener{
        private val titleTextView:TextView =itemView.findViewById(R.id.crime_title)
        private val dateTextView:TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView:ImageView = itemView.findViewById(R.id.crime_solved)
        private lateinit var crime:Crime
        init {
            itemView.setOnClickListener(this)
        }

        fun binding(crime:Crime){
            this.crime=crime
            titleTextView.text=this.crime.title
            val date =this.crime.date
            dateTextView.text=android.text.format.DateFormat
                    .format("EEE , MMM dd ,yyyy",date)

            solvedImageView.visibility=
                    if (this.crime.isSolved)
                View.VISIBLE
            else
                View.GONE

        }
        override fun onClick(v:View){
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter(var crimes : List<Crime>, diffCallback :DiffUtil.ItemCallback<Crime>) :
            RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view =layoutInflater.inflate(R.layout.list_item_crime,parent,false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]

            holder.binding(crime)
        }
        override fun getItemCount(): Int {
           return crimes.size
        }

    }

    private fun updateUI(crimes:List<Crime>){
        adapter=CrimeAdapter(crimes,diffUtilCallbacks)
        crimeRecycleView.adapter=adapter
    }

    companion object{
        fun newInstance():CrimeListFragment{
            return CrimeListFragment()
        }
    }

}
